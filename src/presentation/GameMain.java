package presentation;

import logic.Historial;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class GameMain extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;
    private NigthRushPanel panelGame;

    public GameMain() {
        init();
    }

    private void init() {
        setTitle("Night Rush");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        
        JPanel menuPanel = crearMenuPrincipal();

        
        panelGame = new NigthRushPanel();
        JPanel panelJuego = new JPanel(new BorderLayout());
        panelJuego.add(panelGame, BorderLayout.CENTER);
        panelJuego.add(crearBarraHistorial(), BorderLayout.SOUTH);

        mainPanel.add(menuPanel, "menu");
        mainPanel.add(panelJuego, "juego");

        add(mainPanel);

        cardLayout.show(mainPanel, "menu");
    }

    private JPanel crearMenuPrincipal() {

        JPanel menu = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int size = 5; // tamaño del cuadro
                for (int y = 0; y < getHeight(); y += size) {
                    for (int x = 0; x < getWidth(); x += size) {

                        boolean oscuro = ((x + y) / size) % 2 == 0;

                        g.setColor(oscuro
                                ? new Color(5, 5, 20)      
                                : new Color(15, 15, 40));  

                        g.fillRect(x, y, size, size);
                    }
                }
            }
        };

        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));

        Font pixel = new Font("Consolas", Font.BOLD, 28);

        
        JLabel titulo = new JLabel("NIGHT RUSH");
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        titulo.setFont(pixel.deriveFont(60f));
        titulo.setForeground(new Color(0, 255, 255)); 

        JLabel subtitulo = new JLabel("MENÚ PRINCIPAL");
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setFont(pixel.deriveFont(24f));
        subtitulo.setForeground(new Color(100, 200, 255));

        JButton btnJugar = crearBotonNeon("Iniciar Juego", pixel);
        JButton btnComoJugar = crearBotonNeon("Cómo jugar", pixel);
        JButton btnTeclas = crearBotonNeon("Controles", pixel);
        JButton btnSalir = crearBotonNeon("Salir", pixel);

        btnJugar.addActionListener(e -> mostrarJuego());

        btnComoJugar.addActionListener(e -> JOptionPane.showMessageDialog(
                null,
                """
                OBJETIVO:
                - Esquiva enemigos.
                - Dispara para sobrevivir.
                - Aumenta tu puntuación.

                MECÁNICAS:
                - Los enemigos te siguen.
                - Puedes disparar.
                - Si tu vida llega a 0 → GAME OVER.
                """,
                "Cómo jugar",
                JOptionPane.INFORMATION_MESSAGE
        ));

        btnTeclas.addActionListener(e -> JOptionPane.showMessageDialog(
                null,
                """
                CONTROLES

                A  → Mover izquierda
                D  → Mover derecha
                W/S → Arriba/abajo
                SPACE → Acelerar
                J  → Disparar
                L  → Escudo
                G  → Guardar partida
                C  → Cargar partida
                ENTER → Reiniciar tras morir
                F → Cargar y eliminar último guardado
                """,
                "Controles",
                JOptionPane.INFORMATION_MESSAGE
        ));

        btnSalir.addActionListener((ActionEvent e) -> System.exit(0));

     
        menu.add(Box.createVerticalStrut(80));
        menu.add(titulo);
        menu.add(Box.createVerticalStrut(10));
        menu.add(subtitulo);
        menu.add(Box.createVerticalStrut(40));
        menu.add(btnJugar);
        menu.add(Box.createVerticalStrut(20));
        menu.add(btnComoJugar);
        menu.add(Box.createVerticalStrut(20));
        menu.add(btnTeclas);
        menu.add(Box.createVerticalStrut(20));
        menu.add(btnSalir);

        return menu;
    }

    
    private JButton crearBotonNeon(String texto, Font pixel) {

        JButton b = new JButton(texto);

        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFont(pixel.deriveFont(22f));
        b.setForeground(new Color(0, 255, 255)); 
        b.setBackground(new Color(10, 10, 30)); 
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 200, 255), 3),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));

        b.setFocusPainted(false);

        return b;
    }

  
    private JPanel crearBarraHistorial() {

        JButton btnHistorial = new JButton("Ver historial");
        JButton btnLimpiar = new JButton("Limpiar historial");

        btnHistorial.addActionListener(e -> {
            List<String> h = Historial.obtenerHistorial();
            String msg = h.isEmpty() ? "No hay partidas registradas." : String.join("\n", h);
            JOptionPane.showMessageDialog(null, msg, "Historial", JOptionPane.INFORMATION_MESSAGE);
            panelGame.requestFocusInWindow();
        });

        btnLimpiar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "¿Deseas eliminar todo el historial?",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                Historial.limpiarHistorial();
                JOptionPane.showMessageDialog(null, "Historial eliminado.");
            }
            panelGame.requestFocusInWindow();
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(btnHistorial);
        panel.add(btnLimpiar);

        return panel;
    }


    private void mostrarJuego() {
        cardLayout.show(mainPanel, "juego");

        SwingUtilities.invokeLater(() -> {
            panelGame.start();
            panelGame.requestFocusInWindow();
        });
    }

    public static void main(String[] args) {
        GameMain main = new GameMain();
        main.setVisible(true);
    }
}
