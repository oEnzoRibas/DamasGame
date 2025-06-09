package controle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
// Potentially need to mock Scanner or provide input streams for more complex tests later
// For now, we'll just test basic setup.

class JogoTest {
    @Test
    void testCriacaoJogo() {
        // This test is very basic, mainly ensuring Jogo can be instantiated.
        // More complex game flow tests would require mocking or input redirection.
        Jogo jogo = new Jogo();
        assertNotNull(jogo);
        // At this point, jogo.iniciar() would run the game loop,
        // which is hard to test without more advanced techniques.
        // We can add assertions if Jogo exposes its state (e.g., players, current player)
        // after initialization but before starting the loop.
    }
}
