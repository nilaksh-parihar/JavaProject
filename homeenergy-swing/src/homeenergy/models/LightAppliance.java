package homeenergy.models;

public class LightAppliance extends Appliance {
    public LightAppliance(String name, int power, int hours) {
        super(name, power, hours);
    }

    @Override
    public double calculateEnergy() {
        return (power * hours) / 1000.0;
    }
}