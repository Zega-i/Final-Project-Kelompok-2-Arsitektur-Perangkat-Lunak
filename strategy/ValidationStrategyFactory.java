package strategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory untuk ValidationStrategy — memisahkan pemilihan strategy dari SubmitFacade.
 * Mendukung Dependency Inversion Principle: Facade hanya bergantung pada interface
 * ValidationStrategy, bukan pada class konkrit masing-masing PKM.
 * Mendukung Open/Closed Principle: tambah jenis PKM baru cukup register di sini
 * tanpa mengubah kode Facade.
 */
public class ValidationStrategyFactory {

    private final Map<String, ValidationStrategy> registry = new HashMap<>();

    public ValidationStrategyFactory() {
        registry.put("PKM-K",  new PkmKValidationStrategy());
        registry.put("PKM-RE", new PkmReValidationStrategy());
        registry.put("PKM-PM", new PkmPmValidationStrategy());
    }

    /**
     * Mengembalikan strategy validasi sesuai jenis PKM.
     * @throws IllegalArgumentException jika jenis PKM tidak dikenal
     */
    public ValidationStrategy getStrategy(String pkmType) {
        ValidationStrategy strategy = registry.get(pkmType);
        if (strategy == null) {
            throw new IllegalArgumentException("Jenis PKM tidak dikenal: " + pkmType);
        }
        return strategy;
    }
}
