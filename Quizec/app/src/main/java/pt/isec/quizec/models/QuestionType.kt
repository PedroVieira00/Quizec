package pt.isec.quizec.models

import pt.isec.quizec.R

enum class QuestionType {
    YES_NO,
    MULTIPLE_CHOICE_ONE_CORRECT,
    MULTIPLE_CHOICE_MULTIPLE_CORRECT,
    MATCHING,
    ORDERING,
    FILL_IN_THE_BLANK,
    CONCEPT_ASSOCIATION,
    WORD_RESPONSE;

    fun getStringResId(): Int {
        return when (this) {
            YES_NO -> R.string.question_type_yes_no
            MULTIPLE_CHOICE_ONE_CORRECT -> R.string.question_type_multiple_choice_one_correct
            MULTIPLE_CHOICE_MULTIPLE_CORRECT -> R.string.question_type_multiple_choice_multiple_correct
            MATCHING -> R.string.question_type_matching
            ORDERING -> R.string.question_type_ordering
            FILL_IN_THE_BLANK -> R.string.question_type_fill_in_the_blank
            CONCEPT_ASSOCIATION -> R.string.question_type_concept_association
            WORD_RESPONSE -> R.string.question_type_word_response
        }
    }
}