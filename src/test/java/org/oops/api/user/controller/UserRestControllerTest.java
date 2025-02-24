package org.oops.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.oops.api.user.dto.UserDTO;
import org.oops.api.user.service.CustomOAuth2UserService;
import org.oops.api.user.service.UserServiceImpl;
import org.oops.domain.user.Role;
import org.oops.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest에 비해 웹과 관련된 빈들만 생성해줘서 단위 테스트에 적절하다.
@WebMvcTest(controllers = {UserRestController.class})

//@WebMvcTest는 테스트에 필요한 모든 빈들을 생성해주지 않기 때문에 필요한 빈들을 목업해줘야 한다.
@MockBeans({
        @MockBean(JpaMetamodelMappingContext.class),
        @MockBean(UserServiceImpl.class),
        @MockBean(CustomOAuth2UserService.class),
        @MockBean(UserRepository.class)
})
class UserRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private UserRepository userRepository;

    @Test
    void findAll() throws Exception {
        mockMvc.perform(get("/api/user/find/all")
                        .with(oauth2Login()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    void findById() throws Exception {
        mockMvc.perform(get("/api/user/find/1")
                        .with(oauth2Login()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user1", roles = {"USER"})
    @DisplayName("사용자 정보 수정 테스트")
    void update() throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        //given
        UserDTO user = UserDTO.builder()
                .userName("user1")
                .phoneNumber("01012341234")
                .email("example@naver.com")
                .role(Role.USER)
                .build();

        when(userServiceImpl.update(anyLong(), any(UserDTO.class))).thenReturn(user);

        //when, then
        mockMvc.perform(put("/api/user/update/1")
                        .with(csrf())
                        .with(oauth2Login())
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName").value("user1"))
                .andExpect(jsonPath("$.phoneNumber").value("01012341234"));
    }

    @Test
    @DisplayName("세션에 이메일이 존재할 때 이메일 반환 테스트")
    void getUserEmail_WithEmailInSession_ShouldReturnEmail() throws Exception {
        // GIVEN
        MockHttpSession session = new MockHttpSession();
        String testEmail = "test@example.com";
        session.setAttribute("email", testEmail);

        // WHEN & THEN
        mockMvc.perform(get("/api/user/email").session(session)
                        .with(csrf())
                        .with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(content().string(testEmail));  // 이메일이 반환되어야 함
    }

    @Test
    @DisplayName("세션에 이메일이 존재하지 않을 때 빈 문자열 반환 테스트")
    void getUserEmail_WithoutEmailInSession_ShouldReturnEmptyString() throws Exception {
        // GIVEN
        MockHttpSession session = new MockHttpSession();  // 이메일 없는 빈 세션

        // WHEN & THEN
        mockMvc.perform(get("/api/user/email").session(session)
                .with(csrf())
                .with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(content().string(""));  // 빈 문자열이 반환되어야 함
    }
}