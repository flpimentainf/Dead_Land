package DEAD.LAND.world;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class tileMap {
	private static final int VAZIO = -1;
	private static final int TILE_CAMA = 0;
	private static final int[] TILE_PORTA = {51, 31};
	public static final int TIPO_CAMA = 1;
	public static final int TIPO_PORTA = 2;
	private static final int ALCANCE_INTERACAO = 16;
	private static final int SEM_INTERACAO = 0;

	private tiles pecaDoCenario;
	private int [][] cenarioValido;
	private int [][] camadaObjetosValida;
	private int cenarioAtualIndex = 0;
	private int [][][] todosOsCenarios;
	private int [][][] todosOsObjetos;

	int [][] cenario1DoJogo = {
		{ 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37},
		{ 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37},
		{ 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 60, 39, 39, 39, 39, 39, 39, 39, 39, 60, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37},
		{ 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 60, 39, 39, 39, 39, 39, 39, 39, 39, 60, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37},
		{ 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 60, 39, 39, 39, 39, 39, 39, 39, 39, 60, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37},
		{ 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 60, 39, 39, 39, 39, 39, 39, 39, 39, 60, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37},
		{ 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 60, 39, 39, 39, 39, 39, 39, 39, 39, 60, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37},
		{ 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 60, 39, 39, 39, 39, 39, 39, 39, 39, 60, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37},
		{ 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 60, 60, 23, 24, 31, 32, 24, 26, 60, 60, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37},
		{ 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37, 37},
	};

	int [][] cenario2DoJogo = {
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
	};

	int [][] cenario3DoJogo = {
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 57, 58, 58, 58, 58, 59, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55,  1,  2,  2,  2,  2,  3, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55,  1,  2,  2,  2,  2,  3, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55,  4,  5,  5,  5,  5,  6, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
	};

	int [][] cenario4DoJogo = {
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
		{55, 55, 11, 12, 14, 13, 55, 55, 11, 12, 14, 13, 55, 55, 11, 12, 14, 13, 55, 55, 11, 12, 14, 13, 55, 55, 11, 12, 14, 13, 55, 55},
		{55, 55, 15, 16, 18, 17, 55, 55, 15, 16, 18, 17, 55, 55, 15, 16, 18, 17, 55, 55, 15, 16, 18, 17, 55, 55, 15, 16, 18, 17, 55, 55},
		{55, 55, 19, 20, 27, 22, 55, 55, 19, 20, 27, 22, 55, 55, 19, 20, 27, 22, 55, 55, 19, 20, 27, 22, 55, 55, 19, 20, 27, 22, 55, 55},
		{55, 55, 57, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58},
		{55, 55,  4,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5},
		{55, 55, 11, 14, 12, 13, 55, 55, 55, 55, 11, 14, 12, 13, 55, 55, 55, 55, 11, 14, 12, 13, 55, 55, 55, 55, 11, 14, 12, 13, 55, 55},
		{55, 55, 15, 16, 16, 17, 55, 55, 55, 55, 15, 16, 16, 17, 55, 55, 55, 55, 15, 16, 16, 17, 55, 55, 55, 55, 15, 16, 16, 17, 55, 55},
		{55, 55, 19, 20, 20, 22, 55, 55, 55, 55, 19, 20, 20, 22, 55, 55, 55, 55, 19, 20, 20, 22, 55, 55, 55, 55, 19, 20, 20, 22, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
	};

	int [][] cenario5DoJogo = {
		{55, 55, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 43, 44, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 55, 55, 55, 55},
		{55, 55, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 51, 52, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 55, 55, 55, 55},
		{55, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36,  1,  3, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55,  1,  3, 55, 55, 55, 36, 36, 36, 36, 36, 36, 36, 36, 36, 55, 55, 55, 55},
		{58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,  7,  3, 55, 55, 55, 36, 36, 36, 36, 36, 36, 36, 36, 36, 55, 55, 55, 55},
		{ 5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  5,  6, 55, 55, 55, 36, 36, 36, 36, 36, 36, 36, 36, 36, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 36, 36, 36, 36, 36, 36, 36, 36, 36, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 36, 36, 36, 36, 36, 36, 36, 36, 36, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 36, 36, 36, 36, 36, 36, 36, 36, 36, 55, 55, 55, 55},
		{55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55, 55},
	};

	int [][] cenario6DoJogo = {
		{60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 31, 32, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60},
		{60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 41, 41, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60},
	};

		int [][] cenario7DoJogo = {
		{60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60},
		{60, 41, 41, 41, 41, 60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60, 46, 47, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 41, 45, 41, 41, 60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 41, 41, 41, 41, 60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 60, 60, 41, 41, 60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60, 60, 60, 60, 60, 60, 60, 41, 41, 60, 60},
		{60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41},
		{60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41},
		{60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 60},
		{60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60, 60},
	};

	int [][] cenario8DoJogo = {
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 33, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 34, 35, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 40, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 42, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 40, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 42, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 40, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 42, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 40, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 41, 42, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 48, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 49, 50, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
		{ 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38, 38},
	};

	int[][] cenario1Objetos = {
		{ VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO },
		{ VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO },
		{ VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO },
		{ VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,     0, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO },
		{ VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO },
		{ VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO },
		{ VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO },
		{ VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO },
		{ VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO },
		{ VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO },
	};

	int[][] cenario3Objetos = {
		{56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56},
		{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,56},
		{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,56},
		{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,VAZIO},
		{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,VAZIO},
		{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,VAZIO},
		{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,VAZIO},
		{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,56},
		{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,56},
		{56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56},
	};
	
	int[][] cenario4Objetos = {
			{56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56},
			{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,56},
			{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,56},
			{VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,VAZIO},
			{VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,VAZIO},
			{VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,VAZIO},
			{VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,VAZIO},
			{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,56},
			{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO,56},
			{56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56},
	};

	int[][] cenario5Objetos = {
			{56, 56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, 56, 56, 56, 56},
			{56, 56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, 51, 52, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, 56, 56, 56,56},
			{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, 56, 56, 56,56},
			{VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, 56, 56, 56,56},
			{VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, 56, 56, 56,56},
			{VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, 56, 56, 56,56},
			{VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, 56, 56, 56,56},
			{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, 56, 56, 56,56},
			{56, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, VAZIO, 56, 56, 56,56},
			{56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56, 56},
	};

	public tileMap() {
		this.pecaDoCenario = new tiles();
		this.todosOsCenarios = new int[][][] {
			this.cenario1DoJogo,
			this.cenario2DoJogo,
			this.cenario3DoJogo,
			this.cenario4DoJogo,
			this.cenario5DoJogo,
			this.cenario6DoJogo,
			this.cenario7DoJogo,
			this.cenario8DoJogo
		};
		this.todosOsObjetos = new int[][][] {
			this.cenario1Objetos,
			criarCamadaVazia(this.cenario2DoJogo),
			this.cenario3Objetos,
			this.cenario4Objetos,
			this.cenario5Objetos,
			criarCamadaVazia(this.cenario6DoJogo),
			criarCamadaVazia(this.cenario7DoJogo),
			criarCamadaVazia(this.cenario8DoJogo)
		};
		this.cenarioAtualIndex = 0;
		atualizarCenarioValido();
	}

	public void irParaProximoCenario() {
		if (this.cenarioAtualIndex < this.todosOsCenarios.length - 1) {
			this.cenarioAtualIndex++;
			atualizarCenarioValido();
		}
	}

	public void irParaCenarioAnterior() {
		if (this.cenarioAtualIndex > 0) {
			this.cenarioAtualIndex--;
			atualizarCenarioValido();
		}
	}

	public void irParaCenario(int indexCenario) {
		if (indexCenario < 0 || indexCenario >= this.todosOsCenarios.length) {
			return;
		}

		this.cenarioAtualIndex = indexCenario;
		atualizarCenarioValido();
	}

	public boolean ehPrimeiroCenario() {
		return this.cenarioAtualIndex <= 0;
	}

	public boolean ehUltimoCenario() {
		return this.cenarioAtualIndex >= this.todosOsCenarios.length - 1;
	}

	public int getCenarioAtualIndex() {
		return this.cenarioAtualIndex;
	}

	private int[][] criarCamadaVazia(int[][] cenarioBase) {
		int[][] camada = new int[cenarioBase.length][cenarioBase[0].length];

		for (int lin = 0; lin < camada.length; lin++) {
			for (int col = 0; col < camada[lin].length; col++) {
				camada[lin][col] = VAZIO;
			}
		}

		return camada;
	}

	private void atualizarCenarioValido() {
		this.cenarioValido = this.todosOsCenarios[this.cenarioAtualIndex];
		this.camadaObjetosValida = this.todosOsObjetos[this.cenarioAtualIndex];
	}

	public void definirTile(int linha, int coluna, int valor) {
		if (linha < 0 || linha >= this.cenarioValido.length) {
			return;
		}
		if (coluna < 0 || coluna >= this.cenarioValido[0].length) {
			return;
		}
		this.cenarioValido[linha][coluna] = valor;
	}

	private void desenharCamada(Graphics2D g, int[][] camada, boolean ignorarVazio) {
		for (int col = 0; col < camada[0].length; col++) {
			for (int lin = 0; lin < camada.length; lin++) {
				int pecaDaMatriz = camada[lin][col];

				if (ignorarVazio && pecaDaMatriz == VAZIO) {
					continue;
				}

				this.pecaDoCenario.carregaPecaDaMatriz(pecaDaMatriz);
				this.pecaDoCenario.desenhar(g, lin, col);
			}
		}
	}

	public void desenhar(Graphics2D g) {
		desenharCamada(g, this.cenarioValido, false);
		desenharCamada(g, this.camadaObjetosValida, true);
	}

	public boolean tileTemColisao(int linha, int coluna) {
		if (linha < 0 || linha >= this.cenarioValido.length) {
			return true;
		}
		if (coluna < 0) {
			return this.ehPrimeiroCenario();
		}
		if (coluna >= this.cenarioValido[0].length) {
			return this.ehUltimoCenario();
		}
		int tileBase = this.cenarioValido[linha][coluna];
		int tileObjeto = this.camadaObjetosValida[linha][coluna];

		return this.pecaDoCenario.isTileSolido(tileBase)
				|| (tileObjeto != VAZIO && this.pecaDoCenario.isTileSolido(tileObjeto));
	}

	public static class InteracaoPerto {
		public Rectangle area;
		public int tipo;

		public InteracaoPerto(Rectangle area, int tipo) {
			this.area = area;
			this.tipo = tipo;
		}
	}

	private boolean ehTilePorta(int tile) {
		for (int porta : TILE_PORTA) {
			if (tile == porta) {
				return true;
			}
		}

		return false;
	}

	private boolean ehTileInterativo(int tile) {
		return tile == TILE_CAMA || ehTilePorta(tile);
	}

	private int getTipoInteracao(int tile) {
		if (tile == TILE_CAMA) {
			return TIPO_CAMA;
		}

		if (ehTilePorta(tile)) {
			return TIPO_PORTA;
		}

		return SEM_INTERACAO;
	}

	public InteracaoPerto getInteracaoPerto(Rectangle areaJogador) {
		Rectangle areaInteracao = new Rectangle(areaJogador);
		areaInteracao.grow(ALCANCE_INTERACAO, ALCANCE_INTERACAO);

		for (int lin = 0; lin < this.camadaObjetosValida.length; lin++) {
			for (int col = 0; col < this.camadaObjetosValida[lin].length; col++) {
				int tileObjeto = this.camadaObjetosValida[lin][col];
				int tileBase = this.cenarioValido[lin][col];
				int tipo = getTipoInteracao(tileObjeto);
				if (tipo == SEM_INTERACAO) {
					tipo = getTipoInteracao(tileBase);
				}

				if (tipo == SEM_INTERACAO) {
					continue;
				}

				Rectangle areaObjeto = new Rectangle(
						col * tiles.LARGURA,
						lin * tiles.ALTURA,
						tiles.LARGURA,
						tiles.ALTURA
				);

				if (areaInteracao.intersects(areaObjeto)) {
					return new InteracaoPerto(areaObjeto, tipo);
				}
			}
		}

		return null;
	}

	public Rectangle getAreaInteracaoPerto(Rectangle areaJogador) {
		InteracaoPerto interacao = getInteracaoPerto(areaJogador);

		if (interacao == null) {
			return null;
		}

		return interacao.area;
	}

	public int getTamanhoTile() {
		return tiles.LARGURA;
	}

	public int getLarguraTotal() {
		return this.cenarioValido[0].length * tiles.LARGURA;
	}

	public int getAlturaTotal() {
		return this.cenarioValido.length * tiles.ALTURA;
	}
	
}
