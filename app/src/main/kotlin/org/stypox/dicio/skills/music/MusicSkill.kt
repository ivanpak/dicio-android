package org.stypox.dicio.skills.music

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.MediaStore
import android.view.KeyEvent
import org.dicio.skill.context.SkillContext
import org.dicio.skill.skill.SkillInfo
import org.dicio.skill.skill.SkillOutput
import org.dicio.skill.standard.StandardRecognizerData
import org.dicio.skill.standard.StandardRecognizerSkill
import org.stypox.dicio.R
import org.stypox.dicio.sentences.Sentences.Music
import org.stypox.dicio.util.getString


class MusicSkill(correspondingSkillInfo: SkillInfo, data: StandardRecognizerData<Music>)
    : StandardRecognizerSkill<Music>(correspondingSkillInfo, data) {
        companion object {
            private fun sendEvent(audioManager: AudioManager, keycode: Int) {
                val event1 = KeyEvent(KeyEvent.ACTION_DOWN, keycode)
                audioManager.dispatchMediaKeyEvent(event1);
                val event2 = KeyEvent(KeyEvent.ACTION_UP, keycode)
                audioManager.dispatchMediaKeyEvent(event2);
            }
        }

    override suspend fun generateOutput(ctx: SkillContext, inputData: Music): SkillOutput {

        var songToPlay: String? = null
        if(inputData is Music.Query) {
            songToPlay = inputData.song ?: return MusicOutput(null, null)
            val intent1 = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)
            intent1.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*")
            intent1.putExtra(SearchManager.QUERY, songToPlay)
            // intent1.setAction("com.google.android.youtube.music.action.play")
            // intent1.setAction("com.google.android.youtube.music.pendingintent.controller_widget_play")
            // intent1.setPackage("com.google.android.apps.youtube.music")
            // intent1.setComponent(ComponentName("com.google.android.apps.youtube.music", "com.google.android.apps.youtube.music.activities.MusicActivity"))
            intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (intent1.resolveActivity(ctx.android.packageManager) != null) {
                ctx.android.startActivity(intent1)
            }

            return MusicOutput(songToPlay, null)
        } else {
            val am = ctx.android.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            when (inputData) {
                is Music.Pause -> {
                    sendEvent(am, KeyEvent.KEYCODE_MEDIA_PAUSE)
                    return MusicOutput(null, ctx.getString(R.string.skill_music_pause))
                }
                is Music.Resume -> {
                    sendEvent(am, KeyEvent.KEYCODE_MEDIA_PLAY)
                    return MusicOutput(null, ctx.getString(R.string.skill_music_resume))
                }
                is Music.Next -> {
                    sendEvent(am, KeyEvent.KEYCODE_MEDIA_NEXT)
                    return MusicOutput(null,ctx.getString(R.string.skill_music_next))
                }
                else -> return MusicOutput(null, null)
            }
        }
    }
}
