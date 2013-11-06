package br.com.redefood.util;

import java.util.InputMismatchException;

public class CNPJValidator {

	public static boolean isCNPJ(String CNPJ) {

		if (!CNPJ.contains(".") && !CNPJ.contains("/") && !CNPJ.contains("/"))
			CNPJ = maskCNPJ(CNPJ);

		// considera-se erro CNPJ's formados por uma sequencia de numeros iguais
		if (CNPJ.equals("00.000.000/0000-00") || CNPJ.equals("11.111.111/1111-11")
				|| CNPJ.equals("22.222.222/2222-22") || CNPJ.equals("33.333.333/3333-33")
				|| CNPJ.equals("44.444.444/4444-44") || CNPJ.equals("55.555.555/5555-55")
				|| CNPJ.equals("66.666.666/6666-66") || CNPJ.equals("77.777.777/7777-77")
				|| CNPJ.equals("88.888.888/8888-88") || CNPJ.equals("99.999.999/9999-99")
				|| (CNPJ.length() != 18))
			return (false);

		CNPJ = unMaskCNPJ(CNPJ);

		char dig13, dig14;
		int sm, i, r, num, peso;

		// "try" - protege o código para eventuais erros de conversao de tipo
		// (int)
		try {
			// Calculo do 1o. Digito Verificador
			sm = 0;
			peso = 2;
			for (i = 11; i >= 0; i--) {
				// converte o i-ésimo caractere do CNPJ em um número:
				// por exemplo, transforma o caractere '0' no inteiro 0
				// (48 eh a posição de '0' na tabela ASCII)
				num = (int) (CNPJ.charAt(i) - 48);
				sm = sm + (num * peso);
				peso = peso + 1;
				if (peso == 10)
					peso = 2;
			}

			r = sm % 11;
			if ((r == 0) || (r == 1))
				dig13 = '0';
			else
				dig13 = (char) ((11 - r) + 48);

			// Calculo do 2o. Digito Verificador
			sm = 0;
			peso = 2;
			for (i = 12; i >= 0; i--) {
				num = (int) (CNPJ.charAt(i) - 48);
				sm = sm + (num * peso);
				peso = peso + 1;
				if (peso == 10)
					peso = 2;
			}

			r = sm % 11;
			if ((r == 0) || (r == 1))
				dig14 = '0';
			else
				dig14 = (char) ((11 - r) + 48);

			// Verifica se os dígitos calculados conferem com os dígitos
			// informados.
			if ((dig13 == CNPJ.charAt(12)) && (dig14 == CNPJ.charAt(13)))
				return (true);
			else
				return (false);
		} catch (InputMismatchException erro) {
			return (false);
		}
	}

	public static String maskCNPJ(String CNPJ) {
		// máscara do CNPJ: 99.999.999/9999-99
		return (CNPJ.substring(0, 2) + "." + CNPJ.substring(2, 5) + "." + CNPJ.substring(5, 8)
				+ "/" + CNPJ.substring(8, 12) + "-" + CNPJ.substring(12, 14));
	}

	public static String unMaskCNPJ(String CNPJ) {
		return CNPJ.replace(".", "").replace("/", "").replace("-", "");
	}
}
