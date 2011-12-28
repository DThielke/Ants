package com.dthielke.ants;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ControlPanel extends JPanel {
    private JSpinner evaporationSpinner;
    private JSpinner diffusionSpinner;
    private JSpinner depositDecaySpinner;
    private JSpinner wanderProbabilitySpinner;
    private JSpinner crowdThresholdSpinner;
    private JSpinner detectionThresholdSpinner;
    private AntSim sim;

    public ControlPanel(AntSim sim) {
        this.sim = sim;
        setupUI();
    }

    private void setupUI() {
        ChangeListener listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                applySettings();
            }
        };

        evaporationSpinner = new JSpinner(new SpinnerNumberModel(0.0005, 0.0, 1.0, 0.0005));
        evaporationSpinner.setEditor(new JSpinner.NumberEditor(evaporationSpinner, "0.0000"));
        evaporationSpinner.addChangeListener(listener);

        diffusionSpinner = new JSpinner(new SpinnerNumberModel(0.001, 0.0, 1.0, 0.0005));
        diffusionSpinner.setEditor(new JSpinner.NumberEditor(diffusionSpinner, "0.0000"));
        diffusionSpinner.addChangeListener(listener);

        depositDecaySpinner = new JSpinner(new SpinnerNumberModel(0.001, 0.0, 1.0, 0.0005));
        depositDecaySpinner.setEditor(new JSpinner.NumberEditor(depositDecaySpinner, "0.0000"));
        depositDecaySpinner.addChangeListener(listener);

        wanderProbabilitySpinner = new JSpinner(new SpinnerNumberModel(0.25, 0.0, 1.0, 0.05));
        wanderProbabilitySpinner.setEditor(new JSpinner.NumberEditor(wanderProbabilitySpinner, "0.00"));
        wanderProbabilitySpinner.addChangeListener(listener);

        crowdThresholdSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
        crowdThresholdSpinner.addChangeListener(listener);

        detectionThresholdSpinner = new JSpinner(new SpinnerNumberModel(0.001, 0.0, 1.0, 0.0005));
        detectionThresholdSpinner.setEditor(new JSpinner.NumberEditor(detectionThresholdSpinner, "0.0000"));
        detectionThresholdSpinner.addChangeListener(listener);

        FormLayout layout = new FormLayout("right:max(40dlu;p):g, 4dlu, 50dlu, 7dlu", "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout, this);
        builder.appendSeparator("Environment Settings");
        builder.append("&Evaporation", evaporationSpinner, true);
        builder.append("&Diffusion", diffusionSpinner, true);
        builder.appendSeparator("Ant Settings");
        builder.append("De&posit Decay", depositDecaySpinner, true);
        builder.append("&Wander Probability", wanderProbabilitySpinner, true);
        builder.append("&Crowd Threshold", crowdThresholdSpinner, true);
        builder.append("Detect&ion Threshold", detectionThresholdSpinner, true);
    }

    public void applySettings() {
        sim.setEvaporation(((Number) evaporationSpinner.getValue()).doubleValue());
        sim.setDiffusion(((Number) diffusionSpinner.getValue()).doubleValue());
        sim.setDepositDecay(((Number) depositDecaySpinner.getValue()).doubleValue());
        sim.setWanderProbability(((Number) wanderProbabilitySpinner.getValue()).doubleValue());
        sim.setCrowdThreshold(((Number) crowdThresholdSpinner.getValue()).intValue());
        sim.setDetectionThreshold(((Number) detectionThresholdSpinner.getValue()).doubleValue());
    }
}
