package org.stypox.dicio.skills.music

import android.app.SearchManager
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import org.dicio.numbers.unit.Number
import org.dicio.skill.context.SkillContext
import org.dicio.skill.skill.SkillInfo
import org.dicio.skill.skill.SkillOutput
import org.dicio.skill.standard.StandardRecognizerData
import org.dicio.skill.standard.StandardRecognizerSkill
import org.stypox.dicio.sentences.Sentences.Music
import java.util.Locale

class MusicSkill(correspondingSkillInfo: SkillInfo, data: StandardRecognizerData<Music>)
    : StandardRecognizerSkill<Music>(correspondingSkillInfo, data) {
    override suspend fun generateOutput(ctx: SkillContext, inputData: Music): SkillOutput {
        val songToPlay: String = when (inputData) {
            is Music.Query -> inputData.song ?: return MusicOutput(null)
        }

        val intent1 = Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)
        intent1.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*")
        intent1.putExtra(SearchManager.QUERY, songToPlay)
        intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        ctx.android.startActivity(intent1)

        return MusicOutput(songToPlay)
    }
}
