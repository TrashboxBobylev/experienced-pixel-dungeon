package com.trashboxbobylev.exppd.shatteredpixeldungeon.sprites;

import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.Shaman;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.actors.mobs.ShamanSupreme;
import com.trashboxbobylev.exppd.shatteredpixeldungeon.effects.Lightning;

public class ShamanSupremeSprite extends ShamanSprite {
    public void zap( int pos ) {

        parent.add( new Lightning( ch.pos, pos, (ShamanSupreme)ch ) );

        turnTo( ch.pos, pos );
        play( zap );
    }
}
