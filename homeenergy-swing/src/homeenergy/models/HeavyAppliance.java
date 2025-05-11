package homeenergy.models;

public class HeavyAppliance extends Appliance {
    public HeavyAppliance(String name, int power, int hours) {
        super(name, power, hours);
    }

    @Override
    public double calculateEnergy() {
        return (power * hours) / 1000.0;
    }
}