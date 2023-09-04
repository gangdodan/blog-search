//package com.search.common.exception.api;
//
//import com.search.common.exception.domain.CustomException;
//import com.search.common.exception.dto.ErrorResponseEntity;
//import com.search.common.exception.enums.ErrorCode;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//
//
//class GlobalExceptionHandlerTest {
//    @InjectMocks
//    private GlobalExceptionHandler globalExceptionHandler;
//    @Mock
//    private RuntimeException runtimeException;
//    @Mock
//    private CustomException customException;
//
//
//    @BeforeEach
//    void init() {
//        MockitoAnnotations.openMocks(this);
//        globalExceptionHandler = new GlobalExceptionHandler();
//    }
//
//    @DisplayName("RuntimeException_Response 객체 생성")
//    @Test
//    void handleRuntimeExceptions() {
//        when(runtimeException.getMessage()).thenReturn("Runtime exception");
//        ResponseEntity<ErrorResponseEntity> response = globalExceptionHandler.handleRuntimeExceptions(runtimeException);
//
//        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
//        assertEquals("Runtime exception", response.getBody().getMessage());
//    }
//
//    @DisplayName("CustomException_Response 객체 생성")
//    @Test
//    void handleCustomExceptions_ReturnsErrorResponseEntity() {
//        when(customException.getMessage()).thenReturn("Custom exception");
//
//        CustomException exception = assertThrows(CustomException.class, () -> {
//            throw new CustomException(ErrorCode.DATA_NOT_FOUND);
//        });
//
//        ResponseEntity<ErrorResponseEntity> response = globalExceptionHandler.handleCustomExceptions(exception);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("데이터를 찾을 수 없습니다.", response.getBody().getMessage());
//    }
//
//}
