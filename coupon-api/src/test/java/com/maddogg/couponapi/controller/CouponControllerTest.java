package com.maddogg.couponapi.controller;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllCoupons_returnsCouponsFromAllRetailers() throws Exception {
        mockMvc.perform(get("/api/v1/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @Test
    void getCouponsForRetailer_walmart_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/retailers/walmart/coupons"))
                .andExpect(status().isOk());
    }

    @Test
    void getCouponsForRetailer_unknownRetailer_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/v1/retailers/target/coupons"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchCoupons_returnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/coupons/search").param("query", "off"))
                .andExpect(status().isOk());
    }
}
