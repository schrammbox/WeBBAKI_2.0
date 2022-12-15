package de.thb.webbaki.controller.form;

import de.thb.webbaki.entity.MasterScenario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ScenarioFormModel {
    List<MasterScenario> masterScenarios;
}
