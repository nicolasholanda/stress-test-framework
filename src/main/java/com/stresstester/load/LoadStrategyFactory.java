package com.stresstester.load;

import com.stresstester.model.LoadProfile;

public class LoadStrategyFactory {

    private LoadStrategyFactory() {}

    public static LoadStrategy create(LoadProfile profile) {
        return switch (profile) {
            case CONSTANT -> new ConstantLoadStrategy();
            case RAMP_UP  -> new RampUpLoadStrategy();
            case SPIKE    -> new SpikeLoadStrategy();
        };
    }
}
