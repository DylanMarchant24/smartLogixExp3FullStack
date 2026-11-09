package cl.duocuc.smartlogix.rabbitadmin;

import cl.duocuc.smartlogix.rabbitadmin.controller.RabbitAdminController;
import cl.duocuc.smartlogix.rabbitadmin.dto.CreateQueueRequest;
import cl.duocuc.smartlogix.rabbitadmin.exception.GlobalExceptionHandler;
import cl.duocuc.smartlogix.rabbitadmin.service.RabbitAdminService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RabbitAdminController.class)
@Import(GlobalExceptionHandler.class)
class RabbitAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RabbitAdminService rabbitAdminService;

    @Test
    void createQueue_conNombreVacio_retornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/rabbit-admin/queues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.name").exists());
    }

    @Test
    void createQueue_conCaracteresInvalidos_retornaBadRequest() throws Exception {
        mockMvc.perform(post("/api/rabbit-admin/queues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"cola con espacios y $%/!\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalles.name").exists());
    }

    @Test
    void createQueue_conNombreValido_retornaCreatedYDtoNeutro() throws Exception {
        mockMvc.perform(post("/api/rabbit-admin/queues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"smartlogix.test.queue\", \"durable\": true, \"deadLetterExchange\": \"smartlogix.dlx\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.mensaje").value("Cola creada correctamente"))
                .andExpect(jsonPath("$.name").value("smartlogix.test.queue"));
    }

    @Test
    void deleteQueue_conCaracteresInvalidosEnPath_retornaBadRequest() throws Exception {
        mockMvc.perform(delete("/api/rabbit-admin/queues/cola invalida!"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void service_createQueue_vinculaDlxEnArgumentosAvanzados() {
        RabbitAdmin rabbitAdmin = mock(RabbitAdmin.class);
        RabbitAdminService service = new RabbitAdminService(rabbitAdmin);

        CreateQueueRequest request = new CreateQueueRequest();
        request.setName("smartlogix.custom.queue");
        request.setDurable(true);
        request.setDeadLetterExchange("smartlogix.dlx");
        request.setDeadLetterRoutingKey("custom.dead");

        service.createQueue(request);

        ArgumentCaptor<Queue> captor = ArgumentCaptor.forClass(Queue.class);
        verify(rabbitAdmin).declareQueue(captor.capture());
        Queue declared = captor.getValue();
        assertEquals("smartlogix.custom.queue", declared.getName());
        assertEquals("smartlogix.dlx", declared.getArguments().get("x-dead-letter-exchange"));
        assertEquals("custom.dead", declared.getArguments().get("x-dead-letter-routing-key"));
    }
}

