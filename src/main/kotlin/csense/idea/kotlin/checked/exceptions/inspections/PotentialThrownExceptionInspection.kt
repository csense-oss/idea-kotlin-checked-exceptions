package csense.idea.kotlin.checked.exceptions.inspections

//class PotentialThrownExceptionInspection : LocalInspectionTool() {
//
//    override fun getDisplayName(): String {
//        return "All potential exceptions In kotlin"
//    }
//
//    override fun getShortName(): String {
//        return "PotentialExceptionsInKotlin"
//    }
//
//    override fun getGroupDisplayName(): String {
//        return Constants.groupName
//    }
//
//    override fun isEnabledByDefault(): Boolean {
//        return true
//    }
//
//    override fun buildVisitor(
//        holder: ProblemsHolder,
//        isOnTheFly: Boolean
//    ): KtVisitorVoid {
//        if (isOnTheFly) {
//            return KtVisitorVoid()
//        }
//
//        return classVisitor { it: KtClass ->
//            //TODO make me
//        }
//    }
//
//}