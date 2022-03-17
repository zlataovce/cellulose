package me.kcra.cellulose.script.annotation

@Target(AnnotationTarget.FILE)
@Repeatable
@Retention(AnnotationRetention.SOURCE)
annotation class CompilerOptions(vararg val options: String)
