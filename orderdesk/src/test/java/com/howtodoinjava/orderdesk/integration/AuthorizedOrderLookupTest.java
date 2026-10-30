package com.howtodoinjava.orderdesk.integration;

import java.security.Principal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import static org.assertj.core.api.Assertions.*;

class AuthorizedOrderLookupTest {
    @Test
    void aliceCanReadHerOwnOrder() {
        var service = new AuthorizedOrderLookup(id -> Optional.of(
            new AuthorizedOrderLookup.StoredOrder(id, "alice", "PACKED")));
        assertThat(service.lookup("alice", "ORD-1042", () -> "alice").status())
            .isEqualTo("PACKED"); // The authenticated customer owns the order.
    }

    @Test
    void aliceCannotReadBobsOrder() {
        var service = new AuthorizedOrderLookup(id -> Optional.of(
            new AuthorizedOrderLookup.StoredOrder(id, "bob", "PACKED")));
        Principal alice = () -> "alice"; // Controlled identity for this unit test.
        assertThatThrownBy(() -> service.lookup("alice", "ORD-1042", alice))
            .isInstanceOfSatisfying(ResponseStatusException.class, failure ->
                assertThat(failure.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
                // Passes because the stored owner is Bob, even though the URL says Alice.
    }

    @Test
    void rejectsUntrustedCustomerPathAndMissingIdentity() {
        var service = new AuthorizedOrderLookup(id -> {
            throw new AssertionError("Rejected requests must not reach the repository");
        });
        assertThatThrownBy(() -> service.lookup("bob", "ORD-1042", () -> "alice"))
            .isInstanceOfSatisfying(ResponseStatusException.class, failure ->
                assertThat(failure.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        assertThatThrownBy(() -> service.lookup("alice", "ORD-1042", null))
            .isInstanceOfSatisfying(ResponseStatusException.class, failure ->
                assertThat(failure.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED));
    }
}
