package com.example.vetclinic.service;

import org.springframework.stereotype.Service;

import com.example.vetclinic.domain.Appointment;

@Service
public class NotificationService {

    private final EmailGateway emailGateway;

    public NotificationService(EmailGateway emailGateway) {
        this.emailGateway = emailGateway;
    }

    public void notifyScheduled(Appointment appointment) {
        send(appointment, "Consulta agendada",
                "A consulta de " + appointment.getPet().getName() + " foi agendada para "
                        + appointment.getScheduledAt() + " com " + appointment.getVeterinarian().getName() + ".");
    }

    public void notifyCancelled(Appointment appointment) {
        send(appointment, "Consulta cancelada",
                "A consulta de " + appointment.getPet().getName() + " em " + appointment.getScheduledAt()
                        + " foi cancelada. Valor a pagar: R$ " + appointment.getFee());
    }

    public void notifyCompleted(Appointment appointment) {
        send(appointment, "Consulta concluída",
                "A consulta de " + appointment.getPet().getName() + " foi concluída. Valor: R$ "
                        + appointment.getFee());
    }

    private void send(Appointment appointment, String subject, String body) {
        try {
            emailGateway.send(appointment.getPet().getTutor().getEmail(), subject, body);
        } catch (Exception e) {
            // a notification failure must never break the appointment flow
        }
    }
}
