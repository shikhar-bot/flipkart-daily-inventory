import java.util.*;
import java.util.stream.Collectors;

// Exception classes
class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}

class DuplicateItemException extends RuntimeException {
    public DuplicateItemException(String message) {
        super(message);
    }
}

// Model class
class Item {
    String brand;
    String category;
    int price;
    int quantity;

    public Item(String brand, String category, int price) {
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
        this.brand = brand;
        this.category = category;
        this.price = price;
        this.quantity = 0;
    }

    void addQuantity(int qty) {
        if (qty < 0) throw new IllegalArgumentException("Quantity cannot be negative.");
        this.quantity += qty;
    }

    @Override
    public String toString() {
        return brand + ", " + category + ", " + quantity;
    }
}

// Service Layer
class InventoryService {
    private final Map<String, Item> inventory = new HashMap<>();

    private String getKey(String brand, String category) {
        return brand.toLowerCase() + "#" + category.toLowerCase();
    }

    public void addItem(String brand, String category, int price) {
        String key = getKey(brand, category);
        if (inventory.containsKey(key)) {
            throw new DuplicateItemException("Item already exists: " + brand + ", " + category);
        }
        inventory.put(key, new Item(brand, category, price));
        System.out.println("AddItem(" + brand + ", " + category + ", " + price + ")");
    }

    public void addInventory(String brand, String category, int quantity) {
        String key = getKey(brand, category);
        if (!inventory.containsKey(key)) {
            throw new ItemNotFoundException("Cannot add inventory. Item not found: " + brand + ", " + category);
        }
        inventory.get(key).addQuantity(quantity);
        System.out.println("AddInventory(" + brand + ", " + category + ", " + quantity + ")");
    }

    public List<Item> searchItems(Map<String, List<String>> filters, int[] priceRange, String orderBy, boolean asc) {
        return inventory.values().stream()
                .filter(item -> {
                    if (filters.containsKey("brand") &&
                            !filters.get("brand").stream().map(String::toLowerCase).collect(Collectors.toSet()).contains(item.brand.toLowerCase()))
                        return false;

                    if (filters.containsKey("category") &&
                            !filters.get("category").stream().map(String::toLowerCase).collect(Collectors.toSet()).contains(item.category.toLowerCase()))
                        return false;

                    if (priceRange != null) {
                        int from = priceRange[0];
                        int to = priceRange[1];
                        if ((from != -1 && item.price < from) || (to != -1 && item.price > to))
                            return false;
                    }

                    return true;
                })
                .sorted((a, b) -> {
                    int cmp;
                    switch (orderBy.toLowerCase()) {
                        case "itemqty":
                            cmp = Integer.compare(a.quantity, b.quantity);
                            break;
                        case "price":
                        default:
                            cmp = Integer.compare(a.price, b.price);
                    }
                    return asc ? cmp : -cmp;
                })
                .collect(Collectors.toList());
    }

    public void printInventory() {
        System.out.println("\nInventory:");
        Map<String, Map<String, Integer>> result = new TreeMap<>();
        for (Item item : inventory.values()) {
            result.putIfAbsent(item.brand, new TreeMap<>());
            result.get(item.brand).put(item.category, item.quantity);
        }
        for (String brand : result.keySet()) {
            for (String category : result.get(brand).keySet()) {
                System.out.println(brand + " -> " + category + " -> " + result.get(brand).get(category));
            }
        }
    }

    public void printSearchResults(List<Item> items) {
        if (items.isEmpty()) {
            System.out.println("No items matched the search.");
        } else {
            items.forEach(System.out::println);
        }
    }
}

// Driver class
public class Main {
    public static void main(String[] args) {
        InventoryService service = new InventoryService();

        try {
            // Add Items
            service.addItem("Amul", "Milk", 100);
            service.addItem("Amul", "Curd", 50);
            service.addItem("Nestle", "Milk", 60);
            service.addItem("Nestle", "Curd", 90);

            // Add Inventory
            service.addInventory("Amul", "Milk", 10);
            service.addInventory("Nestle", "Milk", 5);
            service.addInventory("Nestle", "Curd", 10);
            service.addInventory("Amul", "Milk", 10);
            service.addInventory("Amul", "Curd", 5);

            // Print Inventory
            service.printInventory();

            // Search by brand
            System.out.println("\nSearch by brand = Nestle:");
            Map<String, List<String>> filterBrand = new HashMap<>();
            filterBrand.put("brand", Arrays.asList("Nestle"));
            service.printSearchResults(service.searchItems(filterBrand, null, "price", true));

            // Search by category
            System.out.println("\nSearch by category = Milk:");
            Map<String, List<String>> filterCategory = new HashMap<>();
            filterCategory.put("category", Arrays.asList("Milk"));
            service.printSearchResults(service.searchItems(filterCategory, null, "price", true));

            // Search by category, order by price desc
            System.out.println("\nSearch by category = Milk, Order by price desc:");
            service.printSearchResults(service.searchItems(filterCategory, null, "price", false));

            // Search by price range
            System.out.println("\nSearch by price = [70, 100]:");
            int[] priceRange = new int[]{70, 100};
            service.printSearchResults(service.searchItems(new HashMap<>(), priceRange, "price", true));

            // Combined filters
            System.out.println("\nSearch by category = Milk and price range [70, 100], Order by price desc:");
            service.printSearchResults(service.searchItems(filterCategory, priceRange, "price", false));

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
