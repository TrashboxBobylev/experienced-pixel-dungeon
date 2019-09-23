/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2019 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.trashboxbobylev.exppd.shatteredpixeldungeon.items;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.Assets;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.Dungeon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.armor.Armor;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.messages.Messages;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.scenes.InterlevelScene;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.utils.GLog;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.windows.WndBag;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.windows.WndItem;
import com.watabou.noosa.Game;
import com.watabou.noosa.audio.Sample;

import java.util.ArrayList;

public class BrokenSeal extends Item {

	public static final String AC_AFFIX = "AFFIX";
    public static final String AC_CRUSH = "CRUSH";

	//only to be used from the quickslot, for tutorial purposes mostly.
	public static final String AC_INFO = "INFO_WINDOW";

	{
		image = ItemSpriteSheet.SEAL;

		cursedKnown = levelKnown = true;
		unique = true;
		bones = false;

		defaultAction = AC_INFO;
	}

	@Override
	public ArrayList<String> actions(Hero hero) {
		ArrayList<String> actions =  super.actions(hero);
		actions.add(AC_AFFIX);
		return actions;
	}

	@Override
	public void execute(Hero hero, String action) {

		super.execute(hero, action);

		if (action.equals(AC_AFFIX)){
			curItem = this;
			GameScene.selectItem(armorSelector, WndBag.Mode.ARMOR, Messages.get(this, "prompt"));
		} else if (action.equals(AC_INFO)) {
			GameScene.show(new WndItem(null, this, true));
		} else if (action.equals(AC_CRUSH)){
            InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
            Game.switchScene(InterlevelScene.class);
        }
	}

	@Override
	//scroll of upgrade can be used directly once, same as upgrading armor the seal is affixed to then removing it.
	public boolean isUpgradable() {
		return level() == 0;
	}

	protected static WndBag.Listener armorSelector = new WndBag.Listener() {
		@Override
		public void onSelect( Item item ) {
			if (item != null && item instanceof Armor) {
				Armor armor = (Armor)item;
				if (!armor.levelKnown){
					GLog.w(Messages.get(BrokenSeal.class, "unknown_armor"));
				} else if (armor.cursed || armor.level() < 0){
					GLog.w(Messages.get(BrokenSeal.class, "degraded_armor"));
				} else {
					GLog.p(Messages.get(BrokenSeal.class, "affix"));
					Dungeon.hero.sprite.operate(Dungeon.hero.pos);
					Sample.INSTANCE.play(Assets.SND_UNLOCK);
					armor.affixSeal((BrokenSeal)curItem);
					curItem.detach(Dungeon.hero.belongings.backpack);
				}
			}
		}
	};

	public static class WarriorShield extends ShieldBuff {

		private Armor armor;
		private float partialShield;

		@Override
		public synchronized boolean act() {
			if (shielding() < maxShield()) {
				partialShield += 1/(35*Math.pow(0.9f, (maxShield() - shielding() - 1)));
			}
			
			while (partialShield >= 1){
				incShield();
				partialShield--;
			}
			
			if (shielding() <= 0 && maxShield() <= 0){
				detach();
			}
			
			spend(TICK);
			return true;
		}
		
		public synchronized void supercharge(int maxShield){
		    if (maxShield > target.HT) maxShield = target.HT;
			if (maxShield > shielding()){
				setShield(maxShield);
			}
		}

		public synchronized void setArmor(Armor arm){
			armor = arm;
		}

		public synchronized int maxShield() {
			if (armor != null && armor.isEquipped((Hero)target)) {
                int i = 1 + armor.tier + armor.level();
                if (i > target.HT) i = target.HT;
                return i;
			} else {
				return 0;
			}
		}
		
		@Override
		//logic edited slightly as buff should not detach
		public int absorbDamage(int dmg) {
			if (shielding() >= dmg){
				decShield(dmg);
				dmg = 0;
			} else {
				dmg -= shielding();
				setShield(0);
			}
			return dmg;
		}
	}
}
