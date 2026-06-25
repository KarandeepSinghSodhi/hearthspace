package com.widget.shared.domain.pet

import java.util.Calendar

object PetStateMachine {
    /**
     * Derives the current pet state based on the time of day.
     * 22:00–06:00 → Sleepy
     * 06:00–18:00 → Happy
     * 18:00–22:00 → Playful
     */
    fun stateForNow(): PetState {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 22..23, in 0..5 -> PetState.Sleepy
            in 18..21 -> PetState.Playful
            else -> PetState.Happy
        }
    }

    /** Transition triggered by an explicit user tap on the pet. */
    fun onTap(current: PetState): PetState = when (current) {
        PetState.Happy -> PetState.Playful
        PetState.Playful -> PetState.Happy
        PetState.Sleepy -> PetState.Happy
    }

    /** Convert a raw string (stored in Firestore) to a PetState. */
    fun fromString(state: String): PetState = when (state.uppercase()) {
        "SLEEPY" -> PetState.Sleepy
        "PLAYFUL" -> PetState.Playful
        else -> PetState.Happy
    }
}
