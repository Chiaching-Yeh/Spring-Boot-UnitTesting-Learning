package com.self.demo.controller;

import com.self.demo.model.User;
import com.self.demo.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mvc;  // 模擬 HTTP 請求並檢查回應

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;   // 讓 Controller 透過建構子注入 UserService

    /**
     * 在 @BeforeEach 的 Mockito.reset(userService)：
     * 清除前一個測試的狀態，避免資料殘留影響下一個測試。
     *
     * 在測試方法內的 when(...).thenReturn(...) / when(...).thenThrow(...)：
     * 這是 Mockito stub 行為，告訴假物件在特定輸入時要回什麼或丟什麼例外。
     *
     * 因為這個 userService 就是 ServiceMocks 裡 Mockito.mock() 出來的，所以可以自由控制行為而不會去動真實 DB 或邏輯。
     */
    @BeforeEach
    void setUp() {
        // 1. 重置 Mock 狀態 - 清除所有之前的設定
        Mockito.reset(userService);
        // reset() 會清除：
        // 1. 所有的 Stubbing（when().thenReturn() 設定）
        // 2. 所有的 Interaction 記錄（方法呼叫歷史）
        // 3. 所有的 Verification 計數
        // 4. 所有的 Answer 設定
        // 5. 所有的 Spy 設定（如果有的話）

        // 2. 初始化測試環境
        mvc = MockMvcBuilders.standaloneSetup(userController).build();

        // 現在每個測試都有乾淨的 Mock 物件！
    }

    @Test
    void get_shouldReturn200_andBody() throws Exception {
        System.out.println("UserControllerTest-get_shouldReturn200_andBody");
        User u = new User();
        u.setUserId("u1"); u.setName("Tom"); u.setEmail("tom@example.com"); u.setCardId("CARD123");

        // 如果 UserController 在測試中呼叫了 userService.findById("u1")，
        // 就回傳我事先準備好的 User 物件 u。
        // 這樣 Controller 就不會真的去呼叫資料庫。
        when(userService.findById("u1")).thenReturn(u);

        // MockMvc 「在不啟動真實伺服器的情況下，模擬一個 HTTP 請求（GET、POST、PUT...）發送到 Spring MVC。」
        mvc.perform(get("/api/u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("u1"))
                .andExpect(jsonPath("$.name").value("Tom"))
                .andExpect(jsonPath("$.email").value("tom@example.com"))
                .andExpect(jsonPath("$.cardId").value("CARD123"));
    }

    @Test
    void get_shouldReturn404_whenServiceThrows() throws Exception {
        System.out.println("UserControllerTest-get_shouldReturn404_whenServiceThrows");


        // 當傳入 "x" 時，直接丟 NoSuchElementException，
        // 測試 Controller 是否能正確處理錯誤。
        when(userService.findById("x")).thenThrow(new NoSuchElementException("not found"));

        mvc.perform(get("/api/x"))
                .andExpect(status().isNotFound())
                .andExpect(status().reason("not found")); // 你的 controller 將任何錯誤轉為 404/not found
    }

}