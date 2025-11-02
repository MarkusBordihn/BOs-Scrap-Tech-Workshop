/*
 * Copyright 2025 Markus Bordihn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package de.markusbordihn.scraptechworkshop.menu;

import de.markusbordihn.scraptechworkshop.data.energy.EnergyFlowStatus;
import de.markusbordihn.scraptechworkshop.data.energy.ExternalEnergyFlowStatus;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;

public abstract class EnergyPowerGeneratorMenu extends BaseMenu {

  public static final int ENERGY_TAB_BATTERY_SLOT_X = -20;
  public static final int ENERGY_TAB_BATTERY_SLOT_Y = 6;

  protected static final int ENERGY_DATA_INDEX = 0;
  protected static final int ENERGY_CAPACITY_DATA_INDEX = 1;
  protected static final int ENERGY_FLOW_STATUS_DATA_INDEX = 2;
  protected static final int EXTERNAL_ENERGY_FLOW_STATUS_DATA_INDEX = 3;
  protected static final int ENERGY_RECEIVE_AMOUNT_DATA_INDEX = 4;
  protected static final int ENERGY_DISTRIBUTE_AMOUNT_DATA_INDEX = 5;
  protected final ContainerData energyData;

  protected EnergyPowerGeneratorMenu(MenuType<?> menuType, int windowId, ContainerData energyData) {
    super(menuType, windowId);
    this.energyData = energyData != null ? energyData : new SimpleContainerData(6);
    addDataSlots(this.energyData);
  }

  public int getEnergy() {
    return energyData.get(ENERGY_DATA_INDEX);
  }

  public int getEnergyCapacity() {
    return energyData.get(ENERGY_CAPACITY_DATA_INDEX);
  }

  public EnergyFlowStatus getEnergyFlowStatus() {
    int statusOrdinal = energyData.get(ENERGY_FLOW_STATUS_DATA_INDEX);
    EnergyFlowStatus[] statuses = EnergyFlowStatus.values();
    return statusOrdinal >= 0 && statusOrdinal < statuses.length
        ? statuses[statusOrdinal]
        : EnergyFlowStatus.IDLE;
  }

  public ExternalEnergyFlowStatus getExternalEnergyFlowStatus() {
    int statusOrdinal = energyData.get(EXTERNAL_ENERGY_FLOW_STATUS_DATA_INDEX);
    ExternalEnergyFlowStatus[] statuses = ExternalEnergyFlowStatus.values();
    return statusOrdinal >= 0 && statusOrdinal < statuses.length
        ? statuses[statusOrdinal]
        : ExternalEnergyFlowStatus.IDLE;
  }

  public int getEnergyReceiveAmount() {
    return energyData.get(ENERGY_RECEIVE_AMOUNT_DATA_INDEX);
  }

  public int getEnergyDistributeAmount() {
    return energyData.get(ENERGY_DISTRIBUTE_AMOUNT_DATA_INDEX);
  }

  public abstract int getBatterySlotIndex();
}
