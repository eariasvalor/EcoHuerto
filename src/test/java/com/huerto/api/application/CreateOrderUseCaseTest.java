package com.huerto.api.application;

import com.huerto.api.application.commands.CreateOrderCommand;
import com.huerto.api.application.impl.order.CreateOrderUseCaseImpl;
import com.huerto.api.application.usecase.order.CreateOrderResult;
import com.huerto.api.domain.enums.OrderStatus;
import com.huerto.api.domain.enums.Unit;
import com.huerto.api.domain.events.OrderCreatedEvent;
import com.huerto.api.domain.exception.InsufficientStockException;
import com.huerto.api.domain.exception.ResourceNotFoundException;
import com.huerto.api.domain.model.*;
import com.huerto.api.domain.ports.out.CustomerRepository;
import com.huerto.api.domain.ports.out.EventPublisher;
import com.huerto.api.domain.ports.out.OrderRepository;
import com.huerto.api.domain.ports.out.ProductRepository;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Description;
import com.huerto.api.domain.valueobject.Email;
import com.huerto.api.domain.valueobject.Price;
import com.huerto.api.util.CustomerTestFactory;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock OrderRepository orderRepository;
    @Mock ProductRepository productRepository;
    @Mock CustomerRepository customerRepository;
    @Mock EventPublisher eventPublisher;
    @InjectMocks CreateOrderUseCaseImpl createOrderUseCase;


    private Product buildProduct(UUID id, int stock) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        return new Product(id, "Tomato",new Description("Fresh tomato"), variety, Price.of("2.50"), Unit.KG, stock, true, null,0);
    }

    private Order buildOrder(UUID customerId, UUID productId) {
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product = new Product(productId, "Tomato", new Description("Fresh tomato"), variety,
                Price.of("2.50"), Unit.KG, 100, true, null, 0);
        OrderLine line = new OrderLine(UUID.randomUUID(), product, 2);
        return new Order(UUID.randomUUID(), "HUE-0001", customerId, "",
                List.of(line), OrderStatus.PENDING, LocalDateTime.now(), 0);
    }

    @Test
    void should_create_order_without_duplicate_flag_when_no_similar_pending_order() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(customerId);
        Product product = buildProduct(productId, 100);

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 2))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.PENDING))
                .thenReturn(List.of());
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CreateOrderResult result = createOrderUseCase.execute(command);

        assertThat(result.order().lines()).hasSize(1);
        assertThat(result.possibleDuplicate()).isFalse();
    }

    @Test
    void should_create_order_with_duplicate_flag_when_similar_pending_order_exists() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(customerId);
        Product product = buildProduct(productId, 100);
        Order existingOrder = buildOrder(customerId, productId);

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 2))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.PENDING))
                .thenReturn(List.of(existingOrder));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CreateOrderResult result = createOrderUseCase.execute(command);

        assertThat(result.possibleDuplicate()).isTrue();
    }

    @Test
    void should_publish_order_created_event_after_creating() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(customerId);
        Product product = buildProduct(productId, 100);

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 2))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.PENDING))
                .thenReturn(List.of());
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        createOrderUseCase.execute(command);

        ArgumentCaptor<OrderCreatedEvent> captor =
                ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publish(captor.capture());

        assertThat(captor.getValue().order().customerId()).isEqualTo(customerId);
        assertThat(captor.getValue().occurredAt()).isNotNull();
    }

    @Test
    void should_throw_when_customer_not_found() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 2))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> createOrderUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_throw_when_product_not_found() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(customerId);

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 2))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ThrowingCallable execute = () -> createOrderUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(ResourceNotFoundException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_throw_when_stock_is_insufficient() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(customerId);
        Product product = buildProduct(productId, 1);

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 10))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ThrowingCallable execute = () -> createOrderUseCase.execute(command);

        assertThatThrownBy(execute).isInstanceOf(InsufficientStockException.class);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void should_decrease_stock_when_creating_order() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(customerId);
        Product product = buildProduct(productId, 10);

        CreateOrderCommand command = new CreateOrderCommand(
                customerId, List.of(new CreateOrderCommand.OrderLineCommand(productId, 3))
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.PENDING))
                .thenReturn(List.of());
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        createOrderUseCase.execute(command);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());

        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.stock()).isEqualTo(7); // 10 - 3 = 7
    }

    @Test
    void should_decrease_stock_for_multiple_products() {
        UUID customerId = UUID.randomUUID();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        Customer customer = CustomerTestFactory.buildCustomer(customerId);
        
        Variety variety = new Variety(UUID.randomUUID(), "Raf", "Tomato", null);
        Product product1 = new Product(productId1, "Tomato", new Description("Fresh tomato"), variety,
                Price.of("2.50"), Unit.KG, 10, true, null, 0);
        Product product2 = new Product(productId2, "Cucumber", new Description("Fresh cucumber"), variety,
                Price.of("1.50"), Unit.KG, 5, true, null, 0);

        CreateOrderCommand command = new CreateOrderCommand(
                customerId,
                List.of(
                        new CreateOrderCommand.OrderLineCommand(productId1, 2),
                        new CreateOrderCommand.OrderLineCommand(productId2, 3)
                )
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findById(productId1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(productId2)).thenReturn(Optional.of(product2));
        when(orderRepository.findByCustomerIdAndStatus(customerId, OrderStatus.PENDING))
                .thenReturn(List.of());
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        createOrderUseCase.execute(command);

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(2)).save(productCaptor.capture());

        List<Product> savedProducts = productCaptor.getAllValues();
        assertThat(savedProducts).hasSize(2);
        assertThat(savedProducts.get(0).stock()).isEqualTo(8); // 10 - 2 = 8
        assertThat(savedProducts.get(1).stock()).isEqualTo(2); // 5 - 3 = 2
    }
}