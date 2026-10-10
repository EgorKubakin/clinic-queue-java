package lab2.gui;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import lab2.heap.BinHeap;
import lab2.tickets.Ticket;

public class HeapVisualizer extends Canvas {
    private static final int MAX_VISIBLE_NODES = 63;

    public void redraw(BinHeap<Ticket> heap) {
        int visibleSize = Math.min(heap.getSize(), MAX_VISIBLE_NODES);
        int[] visited = heap.getVisited();
        double r = 30;
        int sizeVisited = Math.min(heap.getSizeVisited(), MAX_VISIBLE_NODES);
        int lastIndex = heap.getLastIndex();
        if (visibleSize > 0) {
            int lastLevel = (int) (Math.log(visibleSize) / Math.log(2));
            double horizontalGap = 20;
            double canvasWidth = Math.max(600, (1 << lastLevel) * (2 * r + horizontalGap));
            setWidth(canvasWidth);
        }
        GraphicsContext g = getGraphicsContext2D();
        g.clearRect(0, 0, getWidth(), getHeight());

        for (int i = 1; i < visibleSize; i++) {
            int parent = heap.getParent(i);
            g.strokeLine(getCordX(heap, i), getCordY(heap, i), getCordX(heap, parent), getCordY(heap, parent));
        }
        boolean[] isVisited = new boolean[visibleSize];
        if (visited != null) {
            for (int i = 0; i < sizeVisited; i++) {
                if (visited[i] >= 0 && visited[i] < visibleSize) {
                    isVisited[visited[i]] = true;
                }
            }
        }
        for (int i = 0; i < visibleSize; i++) {
            double x = getCordX(heap, i);
            double y = getCordY(heap, i);
            Ticket ticket = heap.get(i);
            if (i == lastIndex) {
                g.setFill(Color.LIGHTGREEN);
            } else if (isVisited[i]) {
                g.setFill(Color.PEACHPUFF);
            } else {
                g.setFill(Color.ALICEBLUE);
            }
            g.fillOval(x - r, y - r, 2 * r, 2 * r);
            g.setStroke(Color.DARKSLATEGRAY);
            g.strokeOval(x - r, y - r, 2 * r, 2 * r);
            String surname = ticket.getName().trim().split(" ")[0];
            if (surname.length() > 9) {
                surname = surname.substring(0, 9) + "…";
            }
            String time = ticket.getTime().toString();
            g.setFill(Color.BLACK);
            g.setTextAlign(TextAlignment.CENTER);
            g.setTextBaseline(VPos.CENTER);
            g.setFont(Font.font("Arial", 10));
            g.fillText(surname, x, y - 8);
            g.fillText(time, x, y + 8);
        }

    }

    private int getLevel(BinHeap<Ticket> heap, int index) {
        int level = 0;
        while (index > 0) {
            index = heap.getParent(index);
            level++;
        }
        return level;
    }

    private double getCordY(BinHeap<Ticket> heap, int index) {
        int topMargin = 60;
        int verticalGap = 100;
        double y = topMargin + getLevel(heap, index) * verticalGap;
        return y;
    }

    private double getCordX(BinHeap<Ticket> heap, int index) {
        int level = getLevel(heap, index);
        int countNodes = (int) Math.pow(2, level);
        int j = index - (countNodes - 1);
        double x = (2 * (j + 1) - 1) * (getWidth()) / (countNodes * 2);
        return x;
    }
}
