package com.dthielke.ants;

import com.jgoodies.forms.layout.CellConstraints;
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
        setLayout(new FormLayout("left:4dlu:noGrow,fill:max(d;4px):noGrow,left:4dlu:noGrow,fill:150px:grow,left:4dlu:noGrow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow"));

        CellConstraints cc = new CellConstraints();

        ChangeListener listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                applySettings();
            }
        };

        evaporationSpinner = new JSpinner(new SpinnerNumberModel(0.0005, 0.0, 1.0, 0.0005));
        evaporationSpinner.setEditor(new JSpinner.NumberEditor(evaporationSpinner, "0.0000"));
        evaporationSpinner.addChangeListener(listener);
        add(evaporationSpinner, cc.xy(4, 3, CellConstraints.FILL, CellConstraints.DEFAULT));

        diffusionSpinner = new JSpinner(new SpinnerNumberModel(0.001, 0.0, 1.0, 0.0005));
        diffusionSpinner.setEditor(new JSpinner.NumberEditor(diffusionSpinner, "0.0000"));
        diffusionSpinner.addChangeListener(listener);
        add(diffusionSpinner, cc.xy(4, 5, CellConstraints.FILL, CellConstraints.DEFAULT));

        depositDecaySpinner = new JSpinner(new SpinnerNumberModel(0.001, 0.0, 1.0, 0.0005));
        depositDecaySpinner.setEditor(new JSpinner.NumberEditor(depositDecaySpinner, "0.0000"));
        depositDecaySpinner.addChangeListener(listener);
        add(depositDecaySpinner, cc.xy(4, 7, CellConstraints.FILL, CellConstraints.DEFAULT));

        wanderProbabilitySpinner = new JSpinner(new SpinnerNumberModel(0.25, 0.0, 1.0, 0.05));
        wanderProbabilitySpinner.setEditor(new JSpinner.NumberEditor(wanderProbabilitySpinner, "0.00"));
        wanderProbabilitySpinner.addChangeListener(listener);
        add(wanderProbabilitySpinner, cc.xy(4, 9, CellConstraints.FILL, CellConstraints.DEFAULT));

        crowdThresholdSpinner = new JSpinner(new SpinnerNumberModel(10, 0, 1000, 1));
        crowdThresholdSpinner.addChangeListener(listener);
        add(crowdThresholdSpinner, cc.xy(4, 11, CellConstraints.FILL, CellConstraints.DEFAULT));

        detectionThresholdSpinner = new JSpinner(new SpinnerNumberModel(0.001, 0.0, 1.0, 0.0005));
        detectionThresholdSpinner.setEditor(new JSpinner.NumberEditor(detectionThresholdSpinner, "0.0000"));
        detectionThresholdSpinner.addChangeListener(listener);
        add(detectionThresholdSpinner, cc.xy(4, 13, CellConstraints.FILL, CellConstraints.DEFAULT));

        final JLabel label1 = new JLabel();
        label1.setText("Settings");
        label1.setHorizontalAlignment(JLabel.CENTER);
        add(label1, cc.xy(2, 1));
        final JLabel label2 = new JLabel();
        label2.setText("Evaporation");
        label2.setDisplayedMnemonic('E');
        label2.setDisplayedMnemonicIndex(0);
        add(label2, cc.xy(2, 3));
        final JLabel label3 = new JLabel();
        label3.setText("Diffusion");
        label3.setDisplayedMnemonic('D');
        label3.setDisplayedMnemonicIndex(0);
        add(label3, cc.xy(2, 5));
        final JLabel label4 = new JLabel();
        label4.setText("Deposit Decay");
        label4.setDisplayedMnemonic('P');
        label4.setDisplayedMnemonicIndex(2);
        add(label4, cc.xy(2, 7));
        final JLabel label5 = new JLabel();
        label5.setText("Wander Probability");
        label5.setDisplayedMnemonic('W');
        label5.setDisplayedMnemonicIndex(0);
        add(label5, cc.xy(2, 9));
        final JLabel label6 = new JLabel();
        label6.setText("Crowd Threshold");
        label6.setDisplayedMnemonic('C');
        label6.setDisplayedMnemonicIndex(0);
        add(label6, cc.xy(2, 11));
        final JLabel label7 = new JLabel();
        label7.setText("Detection Threshold");
        label7.setDisplayedMnemonic('T');
        label7.setDisplayedMnemonicIndex(10);
        add(label7, cc.xy(2, 13));
        label2.setLabelFor(evaporationSpinner);
        label3.setLabelFor(diffusionSpinner);
        label4.setLabelFor(depositDecaySpinner);
        label5.setLabelFor(wanderProbabilitySpinner);
        label6.setLabelFor(crowdThresholdSpinner);
        label7.setLabelFor(detectionThresholdSpinner);
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
