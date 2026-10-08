package com.fritomix.erp.modules.notifications.application.service;

import com.fritomix.erp.common.dto.PageResponse;
import com.fritomix.erp.exception.ResourceNotFoundException;
import com.fritomix.erp.modules.auth.domain.entity.User;
import com.fritomix.erp.modules.auth.domain.enums.RoleType;
import com.fritomix.erp.modules.auth.domain.repository.UserRepository;
import com.fritomix.erp.modules.notifications.application.dto.request.NotificationRequest;
import com.fritomix.erp.modules.notifications.application.dto.response.NotificationResponse;
import com.fritomix.erp.modules.notifications.domain.entity.Notification;
import com.fritomix.erp.modules.notifications.domain.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailService emailService;

    @Spy
    private EmailTemplateBuilder emailTemplateBuilder = new EmailTemplateBuilder();

    @InjectMocks
    private NotificationService service;

    private User user(long id, String email) {
        return User.builder().id(id).email(email).firstName("Ana").lastName("Soto").build();
    }

    private Notification notification(long id, boolean read) {
        return Notification.builder().id(id).user(user(1L, "ana@x.com"))
                .title("T").message("M").type("INFO").isRead(read).build();
    }

    @Test
    void findRecentByUserIdMapeaPagina() {
        Pageable pageable = PageRequest.of(0, 10);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(notification(1L, false)), pageable, 1));

        PageResponse<NotificationResponse> result = service.findRecentByUserId(1L, pageable);

        assertEquals(1, result.content().size());
        assertEquals("T", result.content().get(0).title());
        assertEquals(Boolean.FALSE, result.content().get(0).isRead());
    }

    @Test
    void countUnreadByUserIdDelega() {
        when(notificationRepository.countByUserIdAndIsReadFalse(1L)).thenReturn(3L);

        assertEquals(3L, service.countUnreadByUserId(1L));
    }

    @Test
    void createUsuarioInexistenteLanzaExcepcion() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        NotificationRequest request = NotificationRequest.builder()
                .userId(9L).title("T").message("M").build();

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void createGuardaNotificacionTipoPorDefecto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "ana@x.com")));

        service.create(NotificationRequest.builder().userId(1L).title("T").message("M").build());

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertEquals("INFO", captor.getValue().getType());
        verify(emailService, never()).sendHtmlEmailQuietly(anyString(), anyString(), anyString());
    }

    @Test
    void createConEmailHabilitadoEnviaCorreo() {
        ReflectionTestUtils.setField(service, "emailEnabled", true);
        ReflectionTestUtils.setField(service, "frontendUrl", "http://app");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L, "ana@x.com")));

        service.create(NotificationRequest.builder()
                .userId(1L).title("T").message("M").link("/x").build());

        verify(emailService).sendHtmlEmailQuietly(eq("ana@x.com"), anyString(), anyString());
    }

    @Test
    void createForRolesGuardaPorCadaUsuario() {
        when(userRepository.findByRoleName("ADMIN"))
                .thenReturn(List.of(user(1L, "a@x.com"), user(2L, "b@x.com")));

        service.createForRoles("T", "M", null, "/x", RoleType.ADMIN);

        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void markAsReadExitoso() {
        Notification n = notification(1L, false);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        service.markAsRead(1L, 1L);

        assertTrue(n.getIsRead());
        verify(notificationRepository).save(n);
    }

    @Test
    void markAsReadDeOtroUsuarioLanzaExcepcion() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification(1L, false)));

        assertThrows(IllegalArgumentException.class, () -> service.markAsRead(1L, 99L));
    }

    @Test
    void markAsReadNoEncontradaLanzaExcepcion() {
        when(notificationRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.markAsRead(9L, 1L));
    }

    @Test
    void markAllAsReadSoloGuardaNoLeidas() {
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(notification(1L, true), notification(2L, false)));

        service.markAllAsRead(1L);

        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);
        verify(notificationRepository).saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertTrue(captor.getValue().get(0).getIsRead());
    }
}