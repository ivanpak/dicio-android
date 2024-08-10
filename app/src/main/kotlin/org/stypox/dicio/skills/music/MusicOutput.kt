package org.stypox.dicio.skills.music

import org.dicio.skill.context.SkillContext
import org.stypox.dicio.R
import org.stypox.dicio.io.graphical.HeadlineSpeechSkillOutput
import org.stypox.dicio.util.getString

class MusicOutput(
    private val song: String?,
    private val action: String?
) : HeadlineSpeechSkillOutput {
    override fun getSpeechOutput(ctx: SkillContext): String {
        return if (song.isNullOrBlank()) {
            if(action.isNullOrBlank()) {
                ctx.getString(R.string.skill_music_specify_song)
            } else {
                action
            }
        } else {
            ctx.getString(R.string.skill_music_playing, song)
        }
    }
}
