import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class MainBD {
    public static void main(String[] args) {

        Properties props=new Properties();
        try {
            props.load(new FileReader("bd.config"));
        } catch (IOException e) {
            System.out.println("Cannot find bd.config "+e);
        }

        // Inițializare repository
        CarRepository carRepo = new CarsDBRepository(props);

        // 1. Adăugare mașină nouă
        Car newCar = new Car("Tesla", "Model S", 2019);
        carRepo.add(newCar);
        System.out.println("Mașină adăugată: " + newCar);

        // 2. Afișare toate mașinile din baza de date
        System.out.println("\nToate mașinile din baza de date:");
        for (Car car : carRepo.findAll()) {
            System.out.println(car);
        }

        // 3. Căutare mașini după producător
        String manufacturer = "Tesla";
        List<Car> teslaCars = carRepo.findByManufacturer(manufacturer);
        System.out.println("\nMașinile produse de " + manufacturer + ":");
        for (Car car : teslaCars) {
            System.out.println(car);
        }

        // 4. Modificare unei mașini existente (presupunem că există o mașină cu ID=1)
        if (!teslaCars.isEmpty()) {
            Car firstTesla = teslaCars.get(0); // Luăm prima Tesla găsită
            int idToUpdate = firstTesla.getId(); // Presupunem că ID-ul este setat corect
            Car updatedCar = new Car("Tesla", "Model X", 2022);
            carRepo.update(idToUpdate, updatedCar);
            System.out.println("\nMașina cu ID " + idToUpdate + " a fost actualizată la: " + updatedCar);
        } else {
            System.out.println("\nNu există mașini Tesla în baza de date pentru a fi actualizate.");
        }

        // 5. Afișare mașini după actualizare
        System.out.println("\nToate mașinile din baza de date după actualizare:");
        for (Car car : carRepo.findAll()) {
            System.out.println(car);
        }
    }
}
