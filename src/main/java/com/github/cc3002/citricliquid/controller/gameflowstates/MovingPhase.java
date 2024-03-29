package com.github.cc3002.citricliquid.controller.gameflowstates;

public class MovingPhase extends TurnPhase {
  int steps;

  public MovingPhase(int steps) {
    setSteps(steps);
  }

  @Override
  public void setSteps(int steps) {
    this.steps = steps;
  }

  @Override
  public int getSteps() {
    return steps;
  }

  @Override
  public void homeStopChoosePhase(int steps) {
    changeTurnPhase(new HomeStopChoosePhase(steps));
  }

  @Override
  public void normaPickPhase() {
    changeTurnPhase(new NormaPickPhase());
  }

  @Override
  public void pathChoosePhase(int steps) {
    changeTurnPhase(new PathChoosePhase(steps));
  }

  @Override
  public void combatChoosePhase(int steps) {
    changeTurnPhase(new CombatChoosePhase(steps));
  }

  @Override
  public void endPhase() {
    changeTurnPhase(new EndPhase());
  }

  @Override
  public boolean isMovingPhase() {
    return true;
  }

}
