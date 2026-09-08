package cl.duocuc.smartlogix.notificaciones.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Envio simulado de correos/alertas.
 * No se conecta a un servidor SMTP real: registra el intento en el log
 * y retorna el resultado para que el servicio de notificaciones actualice el estado.
 * Sirve como punto de extension para integrar un proveedor real (SES, SendGrid, etc.).
 */
@Slf4j
@Service
public class EmailSenderService {

    public EnvioResultado enviar(String destinatario, String asunto, String mensaje) {
        try {
            log.info("[SIMULACION ENVIO] Para: {} | Asunto: {} | Mensaje: {}", destinatario, asunto, mensaje);
            return EnvioResultado.exito();
        } catch (Exception e) {
            log.error("Error simulando envio a {}: {}", destinatario, e.getMessage());
            return EnvioResultado.fallo(e.getMessage());
        }
    }

    public record EnvioResultado(boolean exitoso, String detalleError) {
        public static EnvioResultado exito() {
            return new EnvioResultado(true, null);
        }
        public static EnvioResultado fallo(String detalle) {
            return new EnvioResultado(false, detalle);
        }
    }
}
