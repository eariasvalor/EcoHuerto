package com.huerto.api.util;

import com.huerto.api.application.commands.CreateCustomerCommand;
import com.huerto.api.application.commands.RegisterCustomerCommand;
import com.huerto.api.application.commands.UpdateCustomerCommand;
import com.huerto.api.domain.model.Customer;
import com.huerto.api.domain.valueobject.Credentials;
import com.huerto.api.domain.valueobject.Email;
import com.huerto.api.domain.valueobject.PhoneNumber;
import com.huerto.api.infrastructure.adapters.in.web.dto.RegisterCustomerRequest;
import com.huerto.api.infrastructure.adapters.in.web.dto.UpdateCustomerRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerTestFactory {

    public static Customer buildCustomer(UUID id) {
        return new Customer(
                id,
                "John Doe",
                new Credentials(new Email("john@huerto.com"), "hashed_password"),
                new PhoneNumber("+34", "600123456"),
                null,
                LocalDateTime.now(),
                0
        );
    }

    public static Customer buildCustomer(UUID id, String name) {
        return new Customer(
                id,
                name,
                new Credentials(new Email("john@huerto.com"), "hashed_password"),
                new PhoneNumber("+34", "600123456"),
                null,
                LocalDateTime.now(),
                0
        );
    }

    public static Customer buildCustomer(UUID id, String name, String email) {
        return new Customer(
                id,
                name,
                new Credentials(new Email(email), "hashed_password"),
                new PhoneNumber("+34", "600123456"),
                null,
                LocalDateTime.now(),
                0
        );
    }

    public static RegisterCustomerCommand buildRegisterCommand() {
        return new RegisterCustomerCommand(
                "John Doe",
                "john@huerto.com",
                "secret1234",
                "+34",
                "600123456"
        );
    }

    public static RegisterCustomerCommand buildRegisterCommand(String email) {
        return new RegisterCustomerCommand(
                "John Doe",
                email,
                "secret1234",
                "+34",
                "600123456"
        );
    }

    public static CreateCustomerCommand buildCreateCommand() {
        return new CreateCustomerCommand(
                "John Doe",
                "john@huerto.com",
                "secret1234",
                "+34",
                "600123456",
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public static UpdateCustomerCommand buildUpdateCommand(UUID id) {
        return new UpdateCustomerCommand(
                id,
                "John Updated",
                null,
                "+34",
                "600123456",
                null, null, null, null, null, null, null

        );
    }

    public static UpdateCustomerCommand buildUpdateCommand(UUID id, String rawPassword) {
        return new UpdateCustomerCommand(
                id,
                "John Updated",
                rawPassword,
                "+34",
                "600123456",
                null, null, null, null, null, null, null
        );
    }

    public static RegisterCustomerRequest buildRegisterRequest() {
        return new RegisterCustomerRequest(
                "John Doe",
                "john@huerto.com",
                "secret1234",
                "+34",
                "600123456"
        );
    }

    public static RegisterCustomerRequest buildRegisterRequest(String name, String email, String password) {
        return new RegisterCustomerRequest(
                name,
                email,
                password,
                "+34",
                "600123456"
        );
    }

    public static UpdateCustomerRequest buildUpdateRequest() {
        return new UpdateCustomerRequest("John Updated", null, "+34", "600123456", null, null, null, null, null, null, null);
    }

    public static UpdateCustomerRequest buildUpdateRequest(String name, String rawPassword) {
        return new UpdateCustomerRequest(name, rawPassword, "+34", "600123456", null, null, null, null, null, null, null);
    }


}