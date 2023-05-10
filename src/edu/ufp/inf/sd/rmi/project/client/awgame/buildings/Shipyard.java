package edu.ufp.inf.sd.rmi.project.client.awgame.buildings;

//import edu.ufp.inf.sd.rmi.project.client.awgame.engine.Game;

public class Shipyard extends Base {

	public Shipyard(int owner, int xx, int yy) {
		super(owner, xx, yy);
		name="Capital";
		desc="Creates water units.";
		img = 4;
		Menu = "shipyard";
		//Game.map.map[yy][xx].swim = true;
	}
}
