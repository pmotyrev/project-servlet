package com.tictactoe;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession currentSession = req.getSession();

        Field field = (Field) currentSession.getAttribute("field");
        boolean isGameOver = (boolean) currentSession.getAttribute("gameOver");

        String param = req.getParameter("click");
        int cellIndex = Integer.parseInt(param);

        Sign curentSign = field.getField().get(cellIndex);

        if (Sign.EMPTY != curentSign){
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
            dispatcher.forward(req,resp);
            return;
        }

        if (!isGameOver) {
            field.getField().put(cellIndex, Sign.CROSS);
            if (checkWin(resp, currentSession, field)){
                currentSession.setAttribute("gameOver", true);
                return;
            }
        }

        int emptyFieldIndex = field.getEmptyFieldIndex();


        if (!isGameOver) {
            if (emptyFieldIndex >= 0) {
                field.getField().put(emptyFieldIndex, Sign.NOUGHT);
                if (checkWin(resp, currentSession, field)) {
                    currentSession.setAttribute("gameOver", true);
                    return;
                }
            } else {
                currentSession.setAttribute("draw", true);
                List<Sign> data = field.getFieldData();
                currentSession.setAttribute("data", data);
                resp.sendRedirect("/index.jsp");
                return;
            }
        }

        List<Sign> data = field.getFieldData();

        currentSession.setAttribute("field", field);
        currentSession.setAttribute("data", data);

        resp.sendRedirect("/index.jsp");
    }

    private boolean checkWin(HttpServletResponse response, HttpSession currentSession, Field field) throws IOException {
        Sign winner = field.checkWin();

        if (Sign.CROSS == winner || Sign.NOUGHT == winner){
            currentSession.setAttribute("winner", winner);

            List<Sign> data = field.getFieldData();
            currentSession.setAttribute("data", data);

            response.sendRedirect("/index.jsp");
            return true;
        }

        return false;
    }
}