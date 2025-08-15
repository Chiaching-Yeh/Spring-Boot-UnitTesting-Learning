package com.self.demo.service;

import com.self.demo.dao.UserInterface;
import com.self.demo.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 整體測試目標
 * 驗證 UserService 在不同情境下的行為（找到活躍使用者 vs 找不到或不符合條件的使用者）。
 * 不直接連到資料庫，而是用 Mockito 產生假的 DAO（UserInterface）行為，讓測試專注在 Service 的邏輯。
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    /**
     * @ExtendWith(MockitoExtension.class) 告訴 JUnit 5：這個測試類別會用到 Mockito 功能，需要由 MockitoExtension 幫我們做初始化。
     * 功能：
     * 自動建立 @Mock 標註的假物件。
     * 自動把 @InjectMocks 標註的類別中依賴注入成 Mock。
     * 免去手動呼叫 MockitoAnnotations.openMocks(this)。
     */

    /**
     * 建立一個 假的 DAO 物件，不會去真的連資料庫。
     * 後面可以用 when(...).thenReturn(...) 來指定它在被呼叫時要回什麼。
     */
    @Mock
    UserInterface userDao; // 依賴 → 假物件

    /**
     * 建立被測物件，並把假依賴注入進去。
     */
    @InjectMocks
    UserService userService; // 被測對象

    @Test
    void getById_should_return_user_when_found() {

        // Given（準備）
        String id = "u1";
        User u = new User("u1", "Tom", "tom@example.com", "CARD123");
        when(userDao.findById(id)).thenReturn(Optional.of(u));

        // When（執行）
        User user = userService.findById(id);

        // Then（驗證） 驗證回傳的使用者 id 正確
        assertAll(
                () -> assertEquals("u1", user.getUserId()),
                () -> assertEquals("Tom", user.getName()),
                () -> assertEquals("tom@example.com", user.getEmail()),
                () -> assertEquals("CARD123", user.getCardId())
        );

        // 驗證 userDao.findById(missing) 這個方法在測試執行過程中剛好被呼叫一次。
        // times(1) 是可選的寫法，省略時預設就是 1 次。
        // 如果呼叫次數不符合（多呼叫或少呼叫），測試會失敗。
        // 用途：確保 Service 層有按照預期呼叫 DAO 方法，而且沒有多餘或少掉的查詢。
        verify(userDao, times(1)).findById(id);

        // 驗證在這個測試方法中，除了上面已經驗證過的呼叫之外，userDao 沒有被呼叫過其他任何方法。
        // 如果有其他未驗證的呼叫，測試會失敗。
        // 用途：防止 額外、不必要的 DAO 呼叫，例如 Service 層偷偷多打一次 DB 或呼叫到不該執行的方法。
        verifyNoMoreInteractions(userDao);
    }

    @Test
    void getById_should_throw_NoSuchElementException_when_not_found() {

        String missing = "x";
        when(userDao.findById(missing))
                .thenReturn(Optional.empty());// 或傳回不符合條件的 User

        // 驗證呼叫時會拋出例外
        assertThrows(NoSuchElementException.class,
                () -> userService.findById(missing));

        verify(userDao, times(1)).findById(missing);
        verifyNoMoreInteractions(userDao);
    }

}
