package ro.pub.cs.systems.eim.practicaltest2;

public class Information {
    private final String defintionn;


    public Information(String definition) {
        this.defintionn = definition;
    }

//
//    public String getWindSpeed() {
//        return windSpeed;
//    }
//
//    public String getCondition() {
//        return condition;
//    }
//
//    public String getPressure() {
//        return pressure;
//    }
//
//    public String getHumidity() {
//        return humidity;
//    }

        public String getDefinition() {
        return defintionn;
    }

    @Override
    public String toString() {
        return "Information" + defintionn;
    }
}
