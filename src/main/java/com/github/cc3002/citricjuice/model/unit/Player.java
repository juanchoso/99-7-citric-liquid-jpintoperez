package com.github.cc3002.citricjuice.model.unit;

import com.github.cc3002.citricjuice.model.board.IPanel;
import com.github.cc3002.citricjuice.model.board.NullPanel;
import com.github.cc3002.citricjuice.model.norma.INormaGoal;
import com.github.cc3002.citricjuice.model.norma.NormaFactory;
import com.github.cc3002.citricliquid.controller.GameController;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;


/**
 * This class represents a player in the game 99.7% Citric Liquid.
 *
 * @author <a href="mailto:ignacio.slater@ug.uchile.cl">Ignacio Slater
 *     Muñoz</a>.
 * @version 1.0.6-rc.3
 * @since 1.0
 */
public class Player extends AbstractUnit {

  protected IPanel currentPanel = NullPanel.getNullPanel();
  protected IPanel homePanel = NullPanel.getNullPanel();
  protected INormaGoal goal;
  protected int recoveryLeft;
  // Observable
  private PropertyChangeSupport changes;

  /**
   * Adds an instance of GameController as an observer.
   * @param controller
   *    instance of a game controller to be added to subscribers.
   */
  public void addObserver(GameController controller) {
    changes.addPropertyChangeListener(controller);
  }

  /**
   * Creates a new character.
   *
   * @param name
   *     the character's name.
   * @param hp
   *     the initial (and max) hit points of the character.
   * @param atk
   *     the base damage the character does.
   * @param def
   *     the base defense of the character.
   * @param evd
   *     the base evasion of the character.
   */
  public Player(final String name, final int hp, final int atk, final int def,
                final int evd) {
    super(name, hp, atk, def, evd);
    // Initializes the observable structure
    changes = new PropertyChangeSupport(this);
    normaLevel = 1;
    setNormaGoal(NormaFactory.getStarsNorma(1));
  }



  /**
   * Returns the Norma Goal of this player.
   */
  public INormaGoal getNormaGoal() { return goal; }

  public void setNormaGoal(INormaGoal goal) {
    INormaGoal preGoal = this.goal;
    this.goal = goal;
    changes.firePropertyChange(new PropertyChangeEvent(this, "normaGoal",preGoal,this.goal));
  }

  public boolean normaCheck() {
    return getNormaGoal().normaCheck(this);
  }
  /**
   * Set this unit on a certain panel.
   */
  public void setCurrentPanel(IPanel panel) {
    IPanel prePanel = currentPanel;

    currentPanel.removePlayer(this);
    this.currentPanel = panel;
    currentPanel.addPlayer(this);

    // When assigned to a panel has to check for different cases to
    // notify the observers if something happens

    // Stumbles upon players and might want to fight
    if (panel.getPlayers().size() > 1) {
      changes.firePropertyChange(new PropertyChangeEvent(this, "stumbledUponPlayer",prePanel,panel));
    }

    // Reaches its house
    if (panel.equals(this.getHomePanel())) {
      changes.firePropertyChange(new PropertyChangeEvent(this, "reachedHome",prePanel,panel));
    }

    // Reaches a panel with more than one next panel
    if (panel.getNextPanels().size() > 1) {
      changes.firePropertyChange(new PropertyChangeEvent(this, "reachedPathFork",prePanel,panel));
    }

    changes.firePropertyChange(new PropertyChangeEvent(this, "currentPanel",prePanel,panel));
  }

  /**
   * Gets this unit's current panel.
   */
  public IPanel getCurrentPanel() { return this.currentPanel; }

  /**
   * Set this unit on a certain panel.
   */
  public void setHomePanel(IPanel panel) {
    this.homePanel = panel;
  }

  /**
   * Gets this unit's home panel.
   */
  public IPanel getHomePanel() { return this.homePanel; }

  /**
   * Returns the current norma level
   */
  public int getNormaLevel() {
    return normaLevel;
  }

  /**
   * Sets a certain norma level
   */
  public void setNormaLevel(int level) {
    normaLevel = level;
  }

  /**
   * Performs a norma clear action; the {@code norma} counter increases in 1.
   */
  public void normaClear() {
    normaLevel++;
    changes.firePropertyChange(new PropertyChangeEvent(this, "normaLevel", this.normaLevel-1, this.normaLevel));
  }



  /***
   * Checks whether this player unit is equivalent to another object.
   * @param o Object to compare
   * @return {boolean} True if this unit is equivalent to o.
   */
  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Player)) {
      return false;
    }
    final Player player = (Player) o;
    return getMaxHP() == player.getMaxHP() &&
           getAtk() == player.getAtk() &&
           getDef() == player.getDef() &&
           getEvd() == player.getEvd() &&
           getNormaLevel() == player.getNormaLevel() &&
           getStars() == player.getStars() &&
           getCurrentHP() == player.getCurrentHP() &&
           getName().equals(player.getName());
  }

  /**
   * Changes the value of this player's atk.
   * @param value to replace atk with
   */
  public void setAtk(int value) {
    this.atk = value;
  }

  /**
   * Changes the value of this player's def.
   * @param value to replace def with
   */
  public void setDef(int value) {
    this.def = value;
  }
  /**
   * Changes the value of this player's evd.
   * @param value to replace evd with
   */
  public void setEvd(int value) {
    this.evd = value;
  }

  /**
   * Returns a copy of this character.
   */
  public Player copy() {
    return new Player(name, maxHP, atk, def, evd);
  }

  /**
   * Does a roll to diminish the recoveryLeft count.
   * If it gets the counter on 0 will call the playerRevive method to reset its stats.
   */
  public int recoveryTrial() {
    int val = roll();
    int newCounter = getRecoveryLeft();
    if (isKOd()) {
      newCounter = Math.max(0, getRecoveryLeft() - val);
      setRecoveryLeft(newCounter);

      if (getRecoveryLeft() == 0) {
        playerRevive();
      }

    }

    return newCounter;
  }

  /**
   * Sets the amount of recovery left for this player.
   * @param value
   *    new recovery left value
   */
  public void setRecoveryLeft(int value) {
    recoveryLeft = value;
  }

  /**
   * Returns the amount of recovery score left for this player to come back.
   * @return
   *    amount of recovery score left to come back
   */
  public int getRecoveryLeft() {
    return recoveryLeft;
  }

  /**
   * Restores this unit's max HP, should be called when recovery trial is completed.
   */
  void playerRevive() {
    setCurrentHP(getMaxHP());
  }

  @Override
  void defeatedBy(IUnit attacker) {
    setRecoveryLeft(6);
    attacker.winAgainstPlayer(this);
  }

  @Override
  public void winAgainstPlayer(IUnit player) {
    this.increaseWinsBy(2);
    int getStars = Math.floorDiv(player.getStars(),2);
    this.increaseStarsBy(getStars);
    player.reduceStarsBy(getStars);
    changes.firePropertyChange(new PropertyChangeEvent(this, "wins",this.getWins()-2,this.getWins()));
    changes.firePropertyChange(new PropertyChangeEvent(this, "stars",this.getStars()-getStars,this.getStars()));
  }

  @Override
  public void winAgainstWildUnit(IUnit wildunit) {
    this.increaseWinsBy(1);
    int getStars = wildunit.getStars();
    this.increaseStarsBy(getStars);
    wildunit.reduceStarsBy(getStars);
    changes.firePropertyChange(new PropertyChangeEvent(this, "wins",this.getWins()-1,this.getWins()));
    changes.firePropertyChange(new PropertyChangeEvent(this, "stars",this.getStars()-getStars,this.getStars()));
  }

  @Override
  public void winAgainstBossUnit(IUnit bossunit) {
    this.increaseWinsBy(3);
    int getStars = bossunit.getStars();
    this.increaseStarsBy(getStars);
    bossunit.reduceStarsBy(getStars);
    changes.firePropertyChange(new PropertyChangeEvent(this, "wins",this.getWins()-3,this.getWins()));
    changes.firePropertyChange(new PropertyChangeEvent(this, "stars",this.getStars()-getStars,this.getStars()));
  }

  /**
   * Override of setCurrentHP that also adds Property Change notification
   * @param newHP
   */
  @Override
  public void setCurrentHP(int newHP) {
    int preHP = getCurrentHP();
    super.setCurrentHP(newHP);
    changes.firePropertyChange(new PropertyChangeEvent(this, "HP",preHP,this.getCurrentHP()));
  }

  public void forceEncounter() {
    changes.firePropertyChange(new PropertyChangeEvent(this, "landedOnEncounter",null, null));
  }

  public void forceBossEncounter() {
    changes.firePropertyChange(new PropertyChangeEvent(this, "landedOnBossEncounter",null, null));
  }

}
