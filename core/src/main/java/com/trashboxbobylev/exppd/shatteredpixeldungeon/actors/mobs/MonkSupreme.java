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

package com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.Dungeon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Char;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Amok;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Terror;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.hero.Hero;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.npcs.Imp;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Generator;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Item;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.KindOfWeapon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.food.Food;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.melee.Gauntlet;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.melee.Gloves;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.messages.Messages;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.KingSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.MonkSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class MonkSupreme extends DepthyMob {
	
	{
		spriteClass = KingSprite.class;
		
		HP = HT = 9000;
		
		loot = Generator.randomWeapon();
		lootChance = 0.083f;

		properties.add(Property.UNDEAD);
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( 300, 750 );
	}
	
	@Override
	protected float attackDelay() {
		return super.attackDelay()*0.25f;
	}
	
	@Override
	public int drRoll() {
		return Random.NormalIntRange(500, 1750);
	}
	
	@Override
	public void rollToDropLoot() {
		Imp.Quest.process( this );
		
		super.rollToDropLoot();
	}

	private int hitsToDisarm = 0;
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		
		if (enemy == Dungeon.hero) {
			
			Hero hero = Dungeon.hero;
			KindOfWeapon weapon = hero.belongings.weapon;
			
			if (weapon != null
					&& !(weapon instanceof Gloves)
					&& !(weapon instanceof Gauntlet)
					&& !weapon.cursed) {
				if (hitsToDisarm == 0) hitsToDisarm = Random.NormalIntRange(4, 8);

				if (--hitsToDisarm == 0) {
					hero.belongings.weapon = null;
					Dungeon.quickslot.convertToPlaceholder(weapon);
					Item.updateQuickslot();
					Dungeon.level.drop(weapon, hero.pos).sprite.drop();
					GLog.w(Messages.get(this, "disarm", weapon.name()));
				}
			}
		}
		
		return damage;
	}
	
	{
		immunities.add( Amok.class );
		immunities.add( Terror.class );
	}

	private static String DISARMHITS = "hitsToDisarm";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(DISARMHITS, hitsToDisarm);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		hitsToDisarm = bundle.getInt(DISARMHITS);
	}
}
