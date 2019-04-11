package com.neo.sk.todos2018.front.styles

import scala.language.postfixOps
import scalacss.DevDefaults._

object BYRBBSStyles extends StyleSheet.Inline {
	import dsl._
	val sectionButton = style(
		width(100 px),
		height(38 px),
		borderRadius(5 px),
		fontSize(17 px),
		marginLeft(30 px)
	)
	
	val welcome = style(
		fontSize(25 px),
		margin(20 px),
		textAlign.center
	)
	
	val container = style(
		textAlign.center,
		fontSize(17 px),
		margin(15 px, 0 px),
		height(40 px)
	)
	
	val button = style(
		width(100 px),
		height(38 px),
		borderRadius(5 px),
		fontSize(17 px)
	)
	
	val th = style(
		textAlign.center,
		padding(0 px, 26 px)
	)
	
	val td = style(
		textAlign.left,
	)
	
	val input = style(
		width(160 px),
		height(30 px),
		borderRadius(5 px),
		fontSize(17 px)
	)
	
	val section = style(
		marginTop(20 px),
		width(140 px),
		lineHeight(30 px),
		height(500 px),
		float.left,
		backgroundColor.gray,
		padding(5 px)
	)
	
	val board = style(
		width(300 px),
		height(500 px),
		backgroundColor.ghostwhite,
		padding(10 px),
		float.left
	)
	
	val article = style(
		width(800 px),
		height(500 px),
		backgroundColor.ghostwhite,
		padding(10 px),
		float.left
	)
	
}
