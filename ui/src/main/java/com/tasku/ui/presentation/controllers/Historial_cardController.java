package com.tasku.ui.presentation.controllers;

import com.tasku.ui.client.dto.response.TraceApiResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Historial_cardController {

    private static final Pattern MOVED_PATTERN =
            Pattern.compile("Tarjeta '([^']+)' movida de '([^']+)' a '([^']+)'");
    private static final Pattern CREATED_PATTERN =
            Pattern.compile("Tarjeta '([^']+)' creada en la lista '([^']+)'");

    @FXML private Label avatarLabel;
    @FXML private Text textNombre;
    @FXML private Text textAccion;
    @FXML private Text textTarget;
    @FXML private Label labelDetalle;
    @FXML private Label labelTimestamp;

    public void setTrace(TraceApiResponse trace, Map<String, String> listNames) {
        String email = trace.authorEmail() != null ? trace.authorEmail() : "?";
        String localPart = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;

        avatarLabel.setText(localPart.isEmpty() ? "?" : String.valueOf(localPart.charAt(0)).toUpperCase());

        String description = trace.description() != null ? trace.description() : "";
        for (Map.Entry<String, String> entry : listNames.entrySet()) {
            description = description.replace(entry.getKey(), entry.getValue());
        }

        Matcher moved = MOVED_PATTERN.matcher(description);
        Matcher created = CREATED_PATTERN.matcher(description);
        if (moved.find()) {
            textNombre.setText("Tarjeta ");
            textAccion.setText(moved.group(1));
            textTarget.setText("");
            labelDetalle.setText("movida de '" + moved.group(2) + "' a '" + moved.group(3) + "'");
        } else if (created.find()) {
            textNombre.setText("Tarjeta ");
            textAccion.setText(created.group(1));
            textTarget.setText("");
            labelDetalle.setText("creada en la lista '" + created.group(2) + "'");
        } else {
            textNombre.setText("");
            textAccion.setText("");
            textTarget.setText("");
            labelDetalle.setText(description);
        }

        labelTimestamp.setText(formatDate(trace.date()));
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) return "";
        long days = ChronoUnit.DAYS.between(date.toLocalDate(), LocalDateTime.now().toLocalDate());
        String time = date.format(DateTimeFormatter.ofPattern("HH:mm"));
        if (days == 0) return "Hoy a las " + time;
        if (days == 1) return "Ayer a las " + time;
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " a las " + time;
    }
}
