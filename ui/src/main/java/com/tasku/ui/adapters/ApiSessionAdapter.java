package com.tasku.ui.adapters;

import com.tasku.ui.client.dto.response.BoardApiResponse;
import com.tasku.ui.client.http.TaskuApiClient;
import com.tasku.ui.port.Result;
import com.tasku.ui.port.SessionService;
import com.tasku.ui.state.DesktopSessionState;

import java.util.List;

public class ApiSessionAdapter implements SessionService {
    private final TaskuApiClient apiClient;

    public ApiSessionAdapter(TaskuApiClient apiClient) {
        this.apiClient = apiClient;
    }

    @Override
    public Result<Void> login(String email, String nombre) {
        try {
            DesktopSessionState.setUser(email, nombre);
            List<BoardApiResponse> boards = apiClient.findBoardsByOwner(email);
            if (boards != null && !boards.isEmpty()) {
                BoardApiResponse board = boards.get(0);
                DesktopSessionState.setCurrentBoard(board.url());
            }
            return Result.success(null);
        } catch (Exception e) {
            return Result.failure("Error al iniciar sesión: " + e.getMessage());
        }
    }

    @Override
    public BoardApiResponse getCurrentBoard() {
        String url = DesktopSessionState.getCurrentBoardUrl();
        if (url == null) return null;
        try {
            List<BoardApiResponse> boards = apiClient.findBoardsByOwner(DesktopSessionState.getOwnerEmail());
            if (boards != null) {
                return boards.stream()
                        .filter(b -> url.equals(b.url()))
                        .findFirst()
                        .orElse(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getCurrentUser() {
        return DesktopSessionState.getOwnerEmail();
    }
}
