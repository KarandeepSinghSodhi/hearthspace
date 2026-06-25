// domain/pet/PetState.kt
package com.lovewidget.domain.pet

sealed class PetState(
    val animationRes: String,
    val emotion: String,
    val accessory: String?
) {
    object Happy : PetState("pet_happy.json", "HAPPY", null)
    object Sleepy : PetState("pet_sleepy.json", "SLEEPY", null)
    object Playful : PetState("pet_playful.json", "PLAYFUL", null)
    // Future states can be added here without changing existing logic
}
