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
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.Blob;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.Fire;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Buff;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Burning;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Corrosion;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.Poison;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.npcs.Ghost;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Generator;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.Item;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.missiles.MissileWeapon;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.GnollTricksterSprite;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class GnollSupreme extends DepthyMob {

	{
		spriteClass = GnollTricksterSprite.class;

		HP = HT = 1500;

		//at half quantity, see createLoot()
		loot = Generator.Category.MISSILE;
		lootChance = 1f;
	}

	private int combo = 0;

    @Override
    protected float attackDelay() {
        return super.attackDelay()*0.33f;
    }

    @Override
	protected boolean canAttack( Char enemy ) {
		Ballistica attack = new Ballistica( pos, enemy.pos, Ballistica.PROJECTILE);
		return !Dungeon.level.adjacent(pos, enemy.pos) && attack.collisionPos == enemy.pos;
	}

	@Override
	public int attackProc( Char enemy, int damage ) {
		damage = super.attackProc( enemy, damage );
		//The gnoll's attacks get more severe the more the player lets it hit them
		combo++;
		int effect = Random.Int(4)+combo;

		if (effect > 2) {

			if (effect >=6 && enemy.buff(Burning.class) == null){

				if (Dungeon.level.flamable[enemy.pos])
					GameScene.add(Blob.seed(enemy.pos, 4, ToxicGas.class));
				Buff.affect(enemy, Burning.class).reignite( enemy );

			} else
				Buff.affect( enemy, Corrosion.class).set(effect+2, damage);

		}
		return damage;
	}

    @Override
    public int damageRoll() {
        return Random.NormalIntRange( 150, 200 );
    }

	@Override
	protected boolean getCloser( int target ) {
		combo = 0; //if he's moving, he isn't attacking, reset combo.
		if (state == HUNTING) {
			return enemySeen && getFurther( target );
		} else {
			return super.getCloser( target );
		}
	}
	
	@Override
	protected Item createLoot() {
		MissileWeapon drop = (MissileWeapon)super.createLoot();
		//half quantity, rounded up
		drop.quantity((drop.quantity()+1)/2);
		return drop;
	}

	private static final String COMBO = "combo";

	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle(bundle);
		bundle.put(COMBO, combo);
	}

	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		combo = bundle.getInt( COMBO );
	}

    @Override
    public int drRoll() {
        return Random.NormalIntRange(80, 150);
    }

}
