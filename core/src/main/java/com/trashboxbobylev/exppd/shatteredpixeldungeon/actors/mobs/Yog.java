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
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Actor;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.Char;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.Blob;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.Fire;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.buffs.*;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.Pushing;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.particles.ShadowParticle;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.keys.SkeletonKey;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.scrolls.ScrollOfRetribution;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.scrolls.exotic.ScrollOfPsionicBlast;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.enchantments.Grim;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.items.weapon.enchantments.Precise;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.mechanics.Ballistica;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.messages.Messages;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.scenes.GameScene;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.BurningFistSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.CharSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.LarvaSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.RottingFistSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites.YogSprite;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.ui.BossHealthBar;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.utils.GLog;
import com.watabou.utils.Bundle;
import com.watabou.utils.PathFinder;
import com.watabou.utils.Random;

import java.util.ArrayList;
import java.util.HashSet;

public class Yog extends Mob {
	
	{
		spriteClass = YogSprite.class;
		
		EXP = 500;
		
		state = PASSIVE;

		properties.add(Property.BOSS);
		properties.add(Property.IMMOVABLE);
		properties.add(Property.DEMONIC);
	}
	
	public Yog() {
		super();
        HP = HT = Dungeon.depth == 31? 100000: 300;
	}
	
	public void spawnFists() {
		RottingFist fist1 = new RottingFist();
		BurningFist fist2 = new BurningFist();
		
		do {
			fist1.pos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
			fist2.pos = pos + PathFinder.NEIGHBOURS8[Random.Int( 8 )];
		} while (!Dungeon.level.passable[fist1.pos] || !Dungeon.level.passable[fist2.pos] || fist1.pos == fist2.pos);
		
		GameScene.add( fist1 );
		GameScene.add( fist2 );

		notice();
	}

	@Override
	protected boolean act() {
		//heals 1 health per turn
		HP = Math.min( HT, HP+1 );

        ArrayList<Integer> spawnPoints = new ArrayList<>();

        for (int i=0; i < PathFinder.NEIGHBOURS8.length; i++) {
            int p = pos + PathFinder.NEIGHBOURS8[i];
            if (Actor.findChar( p ) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
                spawnPoints.add( p );
            }
        }

        if (spawnPoints.size() > 0) {
            if (Dungeon.depth == 31 && Random.Float() < 0.33f) {
                Larva larva = new Larva();
                larva.pos = Random.element(spawnPoints);

                if (Dungeon.depth == 31) {
                    larva.HT = larva.HP = 18000;
                    larva.defenseSkill = 300;
                }
                GameScene.add(larva);
                Actor.addDelayed(new Pushing(larva, pos, larva.pos), -1);
            }
        }

        return super.act();
	}

	@Override
	public void damage( int dmg, Object src ) {

		HashSet<Mob> fists = new HashSet<>();

		for (Mob mob : Dungeon.level.mobs)
			if (mob instanceof RottingFist || mob instanceof BurningFist)
				fists.add( mob );

		dmg >>= fists.size();
		
		super.damage( dmg, src );

		LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
		if (lock != null) lock.addTime(dmg*0.5f);

	}
	
	@Override
	public int defenseProc( Char enemy, int damage ) {

		ArrayList<Integer> spawnPoints = new ArrayList<>();
		
		for (int i=0; i < PathFinder.NEIGHBOURS8.length; i++) {
			int p = pos + PathFinder.NEIGHBOURS8[i];
			if (Actor.findChar( p ) == null && (Dungeon.level.passable[p] || Dungeon.level.avoid[p])) {
				spawnPoints.add( p );
			}
		}
		
		if (spawnPoints.size() > 0) {
			Larva larva = new Larva();
			larva.pos = Random.element( spawnPoints );
			
			GameScene.add( larva );
			Actor.addDelayed( new Pushing( larva, pos, larva.pos ), -1 );
		}

		for (Mob mob : Dungeon.level.mobs) {
			if (mob instanceof BurningFist || mob instanceof RottingFist || mob instanceof Larva) {
				mob.aggro( enemy );
			}
		}

		return super.defenseProc(enemy, damage);
	}
	
	@Override
	public void beckon( int cell ) {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void die( Object cause ) {

	    int leftYogs = 0;
	    for (Mob mob : (Iterable<Mob>)Dungeon.level.mobs.clone()){
	        if (mob instanceof Yog) leftYogs++;
        }

        if (leftYogs == 1) death(cause);

        super.die( cause );
    }

    private void death(Object cause) {
        for (Mob mob : (Iterable<Mob>) Dungeon.level.mobs.clone()) {
            if (mob instanceof BurningFist || mob instanceof RottingFist) {
                mob.die( cause );
            }
        }

        GameScene.bossSlain();
        Dungeon.level.drop( new SkeletonKey( Dungeon.depth ), pos ).sprite.drop();

        yell( Messages.get(this, "defeated") );
    }

    @Override
	public void notice() {
		super.notice();
		BossHealthBar.assignBoss(this);
		yell( Messages.get(this, "notice") );
	}
	
	{
		
		immunities.add( Grim.class );
		immunities.add( Terror.class );
		immunities.add( Amok.class );
		immunities.add( Charm.class );
		immunities.add( Sleep.class );
		immunities.add( Burning.class );
		immunities.add( ToxicGas.class );
		immunities.add( ScrollOfRetribution.class );
		immunities.add( ScrollOfPsionicBlast.class );
		immunities.add( Vertigo.class );
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		BossHealthBar.assignBoss(this);
	}

	public static class RottingFist extends Mob {
	
		private static final int REGENERATION	= 4;
		
		{
			spriteClass = RottingFistSprite.class;
			
			EXP = 0;
			
			state = WANDERING;

			properties.add(Property.BOSS);
			properties.add(Property.DEMONIC);
			properties.add(Property.ACIDIC);
		}

        public RottingFist() {
		    super();
            HP = HT = Dungeon.depth == 31? 40000: 300;
            defenseSkill = Dungeon.depth == 31 ? 400 : 30;
        }

        @Override
		public int attackSkill( Char target ) {
            int i = 36;
            if (Dungeon.depth == 31) i = 400;
            return i;
		}
		
		@Override
		public int damageRoll() {
            int i = Random.NormalIntRange(20, 50);
            if (Dungeon.depth == 31) i *= 15;
            return i;
		}
		
		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, 15);
		}
		
		@Override
		public int attackProc( Char enemy, int damage ) {
			damage = super.attackProc( enemy, damage );
			
			if (Random.Int( 3 ) == 0) {
				Buff.affect( enemy, Ooze.class ).set( 20f );
				enemy.sprite.burst( 0xFF000000, 5 );
			}
			
			return damage;
		}
		
		@Override
		public boolean act() {
			
			if (Dungeon.level.water[pos] && HP < HT) {
				sprite.emitter().burst( ShadowParticle.UP, 2 );
				HP += REGENERATION;
			}
			
			return super.act();
		}

		@Override
		public void damage(int dmg, Object src) {
			super.damage(dmg, src);
			LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
			if (lock != null) lock.addTime(dmg*0.5f);
		}
		
		{
			immunities.add( Paralysis.class );
			immunities.add( Amok.class );
			immunities.add( Sleep.class );
			immunities.add( Terror.class );
			immunities.add( Poison.class );
			immunities.add( Vertigo.class );
		}
	}
	
	public static class BurningFist extends Mob {
		
		{
			spriteClass = BurningFistSprite.class;
			
			EXP = 0;
			
			state = WANDERING;

			properties.add(Property.BOSS);
			properties.add(Property.DEMONIC);
			properties.add(Property.FIERY);
		}

        public BurningFist() {
		    super();
            HP = HT = Dungeon.depth == 31? 40000: 300;
            defenseSkill = Dungeon.depth == 31 ? 400 : 25;
        }

        @Override
        public int attackSkill( Char target ) {
            int i = 36;
            if (Dungeon.depth == 31) i = 400;
            return i;
        }
		
		@Override
		public int damageRoll() {
            int i = Random.NormalIntRange(26, 32);
            if (Dungeon.depth == 31) i *= 15;
            return i;
		}
		
		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, 15);
		}
		
		@Override
		protected boolean canAttack( Char enemy ) {
			return new Ballistica( pos, enemy.pos, Ballistica.MAGIC_BOLT).collisionPos == enemy.pos;
		}
		
		@Override
		public boolean attack( Char enemy ) {
			
			if (!Dungeon.level.adjacent( pos, enemy.pos )) {
				spend( attackDelay() );
				
				if (hit( this, enemy, true )) {
					
					int dmg =  damageRoll();
					enemy.damage( dmg, this );
					
					enemy.sprite.bloodBurstA( sprite.center(), dmg );
					enemy.sprite.flash();
					
					if (!enemy.isAlive() && enemy == Dungeon.hero) {
						Dungeon.fail( getClass() );
						GLog.n( Messages.get(Char.class, "kill", name) );
					}
					return true;
					
				} else {
					
					enemy.sprite.showStatus( CharSprite.NEUTRAL,  enemy.defenseVerb() );
					return false;
				}
			} else {
				return super.attack( enemy );
			}
		}
		
		@Override
		public boolean act() {
			
			for (int i=0; i < PathFinder.NEIGHBOURS9.length; i++) {
				GameScene.add( Blob.seed( pos + PathFinder.NEIGHBOURS9[i], 2, Fire.class ) );
			}
			
			return super.act();
		}

		@Override
		public void damage(int dmg, Object src) {
			super.damage(dmg, src);
			LockedFloor lock = Dungeon.hero.buff(LockedFloor.class);
			if (lock != null) lock.addTime(dmg*0.5f);
		}
		
		{
			immunities.add( Amok.class );
			immunities.add( Sleep.class );
			immunities.add( Terror.class );
			immunities.add( Vertigo.class );
		}
	}
	
	public static class Larva extends Mob {
		
		{
			spriteClass = LarvaSprite.class;
			
			HP = HT = 25;
			defenseSkill = 20;
			
			EXP = 0;
			maxLvl = -2;
			
			state = HUNTING;

			properties.add(Property.DEMONIC);
		}
		
		@Override
		public int attackSkill( Char target ) {
			return 18000000;
		}
		
		@Override
		public int damageRoll() {
			return Random.NormalIntRange( 22, 30 );
		}
		
		@Override
		public int drRoll() {
			return Random.NormalIntRange(0, 8);
		}

	}
}
