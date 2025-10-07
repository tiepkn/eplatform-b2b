package com.eplatform.b2b.inventory.unit;

import com.eplatform.b2b.inventory.domain.ProductStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ProductStockTest {

    private ProductStock productStock;

    @BeforeEach
    void setUp() {
        productStock = new ProductStock("TEST-SKU", 10);
    }

    @Test
    void constructor_ShouldInitializeCorrectly() {
        // Then
        assertThat(productStock.getSku()).isEqualTo("TEST-SKU");
        assertThat(productStock.getAvailable()).isEqualTo(10);
        assertThat(productStock.getReserved()).isEqualTo(0);
    }

    @Test
    void add_ShouldIncreaseAvailableStock() {
        // When
        productStock.add(5);

        // Then
        assertThat(productStock.getAvailable()).isEqualTo(15);
        assertThat(productStock.getReserved()).isEqualTo(0);
    }

    @Test
    void reserve_ShouldSucceed_WhenSufficientStock() {
        // When
        boolean result = productStock.reserve(3);

        // Then
        assertThat(result).isTrue();
        assertThat(productStock.getAvailable()).isEqualTo(7);
        assertThat(productStock.getReserved()).isEqualTo(0); // Domain method doesn't use reserved
    }

    @Test
    void reserve_ShouldFail_WhenInsufficientStock() {
        // When
        boolean result = productStock.reserve(15);

        // Then
        assertThat(result).isFalse();
        assertThat(productStock.getAvailable()).isEqualTo(10); // Unchanged
    }

    @Test
    void reserve_ShouldFail_WhenZeroQuantity() {
        // When
        boolean result = productStock.reserve(0);

        // Then
        assertThat(result).isFalse();
        assertThat(productStock.getAvailable()).isEqualTo(10); // Unchanged
    }

    @Test
    void lock_ShouldMoveStockToReserved() {
        // When
        productStock.lock(3);

        // Then
        assertThat(productStock.getAvailable()).isEqualTo(7);
        assertThat(productStock.getReserved()).isEqualTo(3);
    }

    @Test
    void confirmReservation_ShouldRemoveFromReserved() {
        // Given
        productStock.lock(3); // available=7, reserved=3

        // When
        productStock.confirmReservation(2);

        // Then
        assertThat(productStock.getAvailable()).isEqualTo(7); // Unchanged
        assertThat(productStock.getReserved()).isEqualTo(1); // Reduced
    }

    @Test
    void release_ShouldMoveStockBackToAvailable() {
        // Given
        productStock.lock(3); // available=7, reserved=3

        // When
        productStock.release(2);

        // Then
        assertThat(productStock.getAvailable()).isEqualTo(9); // Increased
        assertThat(productStock.getReserved()).isEqualTo(1); // Reduced
    }

    @Test
    void multipleOperations_ShouldWorkCorrectly() {
        // Given - Initial: available=10, reserved=0

        // When - Reserve 3, then lock 2, then confirm 1, then release 1
        productStock.reserve(3); // available=7, reserved=0 (domain method)
        productStock.lock(2);     // available=5, reserved=2
        productStock.confirmReservation(1); // available=5, reserved=1
        productStock.release(1);  // available=6, reserved=0

        // Then
        assertThat(productStock.getAvailable()).isEqualTo(6);
        assertThat(productStock.getReserved()).isEqualTo(0);
    }
}
