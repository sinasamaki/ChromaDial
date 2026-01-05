package com.sinasamaki.chroma.dial

import androidx.compose.ui.graphics.Color

/**
 * Adapted from Tailwind CSS colors
 * https://tailwindcss.com/docs/customizing-colors
 */

internal interface Swatch {
    val v50: Color
    val v100: Color
    val v200: Color
    val v300: Color
    val v400: Color
    val v500: Color
    val v600: Color
    val v700: Color
    val v800: Color
    val v900: Color
    val v950: Color
}

// Slate Colors
internal val Slate50 = Color(0xFFF8FAFC)
internal val Slate100 = Color(0xFFF1F5F9)
internal val Slate200 = Color(0xFFE2E8F0)
internal val Slate300 = Color(0xFFCBD5E1)
internal val Slate400 = Color(0xFF94A3B8)
internal val Slate500 = Color(0xFF64748B)
internal val Slate600 = Color(0xFF475569)
internal val Slate700 = Color(0xFF334155)
internal val Slate800 = Color(0xFF1E293B)
internal val Slate900 = Color(0xFF0F172A)
internal val Slate950 = Color(0xFF020617)

internal object Slate : Swatch {
    override val v50 = Slate50
    override val v100 = Slate100
    override val v200 = Slate200
    override val v300 = Slate300
    override val v400 = Slate400
    override val v500 = Slate500
    override val v600 = Slate600
    override val v700 = Slate700
    override val v800 = Slate800
    override val v900 = Slate900
    override val v950 = Slate950
}

// Gray Colors
internal val Gray50 = Color(0xFFF9FAFB)
internal val Gray100 = Color(0xFFF3F4F6)
internal val Gray200 = Color(0xFFE5E7EB)
internal val Gray300 = Color(0xFFD1D5DB)
internal val Gray400 = Color(0xFF9CA3AF)
internal val Gray500 = Color(0xFF6B7280)
internal val Gray600 = Color(0xFF4B5563)
internal val Gray700 = Color(0xFF374151)
internal val Gray800 = Color(0xFF1F2937)
internal val Gray900 = Color(0xFF111827)
internal val Gray950 = Color(0xFF030712)

internal object Gray : Swatch {
    override val v50 = Gray50
    override val v100 = Gray100
    override val v200 = Gray200
    override val v300 = Gray300
    override val v400 = Gray400
    override val v500 = Gray500
    override val v600 = Gray600
    override val v700 = Gray700
    override val v800 = Gray800
    override val v900 = Gray900
    override val v950 = Gray950
}

// Zinc Colors
internal val Zinc50 = Color(0xFFFAFAFA)
internal val Zinc100 = Color(0xFFF4F4F5)
internal val Zinc200 = Color(0xFFE4E4E7)
internal val Zinc300 = Color(0xFFD4D4D8)
internal val Zinc400 = Color(0xFFA1A1AA)
internal val Zinc500 = Color(0xFF71717A)
internal val Zinc600 = Color(0xFF52525B)
internal val Zinc700 = Color(0xFF3F3F46)
internal val Zinc800 = Color(0xFF27272A)
internal val Zinc900 = Color(0xFF18181B)
internal val Zinc950 = Color(0xFF09090B)

internal object Zinc : Swatch {
    override val v50 = Zinc50
    override val v100 = Zinc100
    override val v200 = Zinc200
    override val v300 = Zinc300
    override val v400 = Zinc400
    override val v500 = Zinc500
    override val v600 = Zinc600
    override val v700 = Zinc700
    override val v800 = Zinc800
    override val v900 = Zinc900
    override val v950 = Zinc950
}

// Neutral Colors
internal val Neutral50 = Color(0xFFFAFAFA)
internal val Neutral100 = Color(0xFFF5F5F5)
internal val Neutral200 = Color(0xFFE5E5E5)
internal val Neutral300 = Color(0xFFD4D4D4)
internal val Neutral400 = Color(0xFFA3A3A3)
internal val Neutral500 = Color(0xFF737373)
internal val Neutral600 = Color(0xFF525252)
internal val Neutral700 = Color(0xFF404040)
internal val Neutral800 = Color(0xFF262626)
internal val Neutral900 = Color(0xFF171717)
internal val Neutral950 = Color(0xFF0A0A0A)

internal object Neutral : Swatch {
    override val v50 = Neutral50
    override val v100 = Neutral100
    override val v200 = Neutral200
    override val v300 = Neutral300
    override val v400 = Neutral400
    override val v500 = Neutral500
    override val v600 = Neutral600
    override val v700 = Neutral700
    override val v800 = Neutral800
    override val v900 = Neutral900
    override val v950 = Neutral950
}

// Stone Colors
internal val Stone50 = Color(0xFFFAFAF9)
internal val Stone100 = Color(0xFFF5F5F4)
internal val Stone200 = Color(0xFFE7E5E4)
internal val Stone300 = Color(0xFFD6D3D1)
internal val Stone400 = Color(0xFFA8A29E)
internal val Stone500 = Color(0xFF78716C)
internal val Stone600 = Color(0xFF57534E)
internal val Stone700 = Color(0xFF44403C)
internal val Stone800 = Color(0xFF292524)
internal val Stone900 = Color(0xFF1C1917)
internal val Stone950 = Color(0xFF0C0A09)

internal object Stone : Swatch {
    override val v50 = Stone50
    override val v100 = Stone100
    override val v200 = Stone200
    override val v300 = Stone300
    override val v400 = Stone400
    override val v500 = Stone500
    override val v600 = Stone600
    override val v700 = Stone700
    override val v800 = Stone800
    override val v900 = Stone900
    override val v950 = Stone950
}

// Red Colors
internal val Red50 = Color(0xFFFEF2F2)
internal val Red100 = Color(0xFFFEE2E2)
internal val Red200 = Color(0xFFFECACA)
internal val Red300 = Color(0xFFFCA5A5)
internal val Red400 = Color(0xFFF87171)
internal val Red500 = Color(0xFFEF4444)
internal val Red600 = Color(0xFFDC2626)
internal val Red700 = Color(0xFFB91C1C)
internal val Red800 = Color(0xFF991B1B)
internal val Red900 = Color(0xFF7F1D1D)
internal val Red950 = Color(0xFF450A0A)

internal object Red : Swatch {
    override val v50 = Red50
    override val v100 = Red100
    override val v200 = Red200
    override val v300 = Red300
    override val v400 = Red400
    override val v500 = Red500
    override val v600 = Red600
    override val v700 = Red700
    override val v800 = Red800
    override val v900 = Red900
    override val v950 = Red950
}


// Orange Colors
internal val Orange50 = Color(0xFFFFF7ED)
internal val Orange100 = Color(0xFFFFEDD5)
internal val Orange200 = Color(0xFFFED7AA)
internal val Orange300 = Color(0xFFFDBA74)
internal val Orange400 = Color(0xFFFB923C)
internal val Orange500 = Color(0xFFF97316)
internal val Orange600 = Color(0xFFEA580C)
internal val Orange700 = Color(0xFFC2410C)
internal val Orange800 = Color(0xFF9A3412)
internal val Orange900 = Color(0xFF7C2D12)
internal val Orange950 = Color(0xFF431407)

internal object Orange : Swatch {
    override val v50 = Orange50
    override val v100 = Orange100
    override val v200 = Orange200
    override val v300 = Orange300
    override val v400 = Orange400
    override val v500 = Orange500
    override val v600 = Orange600
    override val v700 = Orange700
    override val v800 = Orange800
    override val v900 = Orange900
    override val v950 = Orange950
}

// Amber Colors
internal val Amber50 = Color(0xFFFFFBEB)
internal val Amber100 = Color(0xFFFEEFC7)
internal val Amber200 = Color(0xFFFDE68A)
internal val Amber300 = Color(0xFFFCD34D)
internal val Amber400 = Color(0xFFFBBF24)
internal val Amber500 = Color(0xFFF59E0B)
internal val Amber600 = Color(0xFFD97706)
internal val Amber700 = Color(0xFFB45309)
internal val Amber800 = Color(0xFF92400E)
internal val Amber900 = Color(0xFF78350F)
internal val Amber950 = Color(0xFF451A03)

internal object Amber : Swatch {
    override val v50 = Amber50
    override val v100 = Amber100
    override val v200 = Amber200
    override val v300 = Amber300
    override val v400 = Amber400
    override val v500 = Amber500
    override val v600 = Amber600
    override val v700 = Amber700
    override val v800 = Amber800
    override val v900 = Amber900
    override val v950 = Amber950
}

// Yellow Colors
internal val Yellow50 = Color(0xFF_FEFCE8)
internal val Yellow100 = Color(0xFF_FEF9C3)
internal val Yellow200 = Color(0xFF_FEF08A)
internal val Yellow300 = Color(0xFF_FDE047)
internal val Yellow400 = Color(0xFF_FACC15)
internal val Yellow500 = Color(0xFF_EAB308)
internal val Yellow600 = Color(0xFF_CA8A04)
internal val Yellow700 = Color(0xFF_A16207)
internal val Yellow800 = Color(0xFF_854D0E)
internal val Yellow900 = Color(0xFF_713F12)
internal val Yellow950 = Color(0xFF_422006)

internal object Yellow : Swatch {
    override val v50 = Yellow50
    override val v100 = Yellow100
    override val v200 = Yellow200
    override val v300 = Yellow300
    override val v400 = Yellow400
    override val v500 = Yellow500
    override val v600 = Yellow600
    override val v700 = Yellow700
    override val v800 = Yellow800
    override val v900 = Yellow900
    override val v950 = Yellow950
}

// Lime Colors
internal val Lime50 = Color(0xFFF7FEE7)
internal val Lime100 = Color(0xFFECFCCB)
internal val Lime200 = Color(0xFFD9F99D)
internal val Lime300 = Color(0xFFBEF264)
internal val Lime400 = Color(0xFFA3E635)
internal val Lime500 = Color(0xFF84CC16)
internal val Lime600 = Color(0xFF65A30D)
internal val Lime700 = Color(0xFF4D7C0F)
internal val Lime800 = Color(0xFF3F6212)
internal val Lime900 = Color(0xFF365314)
internal val Lime950 = Color(0xFF1A2E05)

internal object Lime : Swatch {
    override val v50 = Lime50
    override val v100 = Lime100
    override val v200 = Lime200
    override val v300 = Lime300
    override val v400 = Lime400
    override val v500 = Lime500
    override val v600 = Lime600
    override val v700 = Lime700
    override val v800 = Lime800
    override val v900 = Lime900
    override val v950 = Lime950
}

// Green Colors
internal val Green50 = Color(0xFFF0FDF4)
internal val Green100 = Color(0xFFDCFCE7)
internal val Green200 = Color(0xFFBBF7D0)
internal val Green300 = Color(0xFF86EFAC)
internal val Green400 = Color(0xFF4ADE80)
internal val Green500 = Color(0xFF22C55E)
internal val Green600 = Color(0xFF16A34A)
internal val Green700 = Color(0xFF15803D)
internal val Green800 = Color(0xFF166534)
internal val Green900 = Color(0xFF14532D)
internal val Green950 = Color(0xFF052E16)

internal object Green : Swatch {
    override val v50 = Green50
    override val v100 = Green100
    override val v200 = Green200
    override val v300 = Green300
    override val v400 = Green400
    override val v500 = Green500
    override val v600 = Green600
    override val v700 = Green700
    override val v800 = Green800
    override val v900 = Green900
    override val v950 = Green950
}

// Emerald Colors
internal val Emerald50 = Color(0xFFECFDF5)
internal val Emerald100 = Color(0xFFD1FAE5)
internal val Emerald200 = Color(0xFFA7F3D0)
internal val Emerald300 = Color(0xFF6EE7B7)
internal val Emerald400 = Color(0xFF34D399)
internal val Emerald500 = Color(0xFF10B981)
internal val Emerald600 = Color(0xFF059669)
internal val Emerald700 = Color(0xFF047857)
internal val Emerald800 = Color(0xFF065F46)
internal val Emerald900 = Color(0xFF064E3B)
internal val Emerald950 = Color(0xFF022C22)

internal object Emerald : Swatch {
    override val v50 = Emerald50
    override val v100 = Emerald100
    override val v200 = Emerald200
    override val v300 = Emerald300
    override val v400 = Emerald400
    override val v500 = Emerald500
    override val v600 = Emerald600
    override val v700 = Emerald700
    override val v800 = Emerald800
    override val v900 = Emerald900
    override val v950 = Emerald950
}

// Teal Colors
internal val Teal50 = Color(0xFFF0FDFA)
internal val Teal100 = Color(0xFFCCFBF1)
internal val Teal200 = Color(0xFF99F6E4)
internal val Teal300 = Color(0xFF5EEAD4)
internal val Teal400 = Color(0xFF2DD4BF)
internal val Teal500 = Color(0xFF14B8A6)
internal val Teal600 = Color(0xFF0D9488)
internal val Teal700 = Color(0xFF0F766E)
internal val Teal800 = Color(0xFF115E59)
internal val Teal900 = Color(0xFF134E4A)
internal val Teal950 = Color(0xFF042F2E)

internal object Teal : Swatch {
    override val v50 = Teal50
    override val v100 = Teal100
    override val v200 = Teal200
    override val v300 = Teal300
    override val v400 = Teal400
    override val v500 = Teal500
    override val v600 = Teal600
    override val v700 = Teal700
    override val v800 = Teal800
    override val v900 = Teal900
    override val v950 = Teal950
}

// Cyan Colors
internal val Cyan50 = Color(0xFFECFEFF)
internal val Cyan100 = Color(0xFFCEFEFE)
internal val Cyan200 = Color(0xFFAFFAF8)
internal val Cyan300 = Color(0xFF67E8F9)
internal val Cyan400 = Color(0xFF22D3EE)
internal val Cyan500 = Color(0xFF06B6D4)
internal val Cyan600 = Color(0xFF0891B2)
internal val Cyan700 = Color(0xFF0E7490)
internal val Cyan800 = Color(0xFF155E75)
internal val Cyan900 = Color(0xFF164E63)
internal val Cyan950 = Color(0xFF083344)

internal object Cyan : Swatch {
    override val v50 = Cyan50
    override val v100 = Cyan100
    override val v200 = Cyan200
    override val v300 = Cyan300
    override val v400 = Cyan400
    override val v500 = Cyan500
    override val v600 = Cyan600
    override val v700 = Cyan700
    override val v800 = Cyan800
    override val v900 = Cyan900
    override val v950 = Cyan950
}

// Sky Colors
internal val Sky50 = Color(0xFFF0F9FF)
internal val Sky100 = Color(0xFFE0F2FE)
internal val Sky200 = Color(0xFFBAE6FD)
internal val Sky300 = Color(0xFF7DD3FC)
internal val Sky400 = Color(0xFF38BDF8)
internal val Sky500 = Color(0xFF0EA5E9)
internal val Sky600 = Color(0xFF0284C7)
internal val Sky700 = Color(0xFF0369A1)
internal val Sky800 = Color(0xFF075985)
internal val Sky900 = Color(0xFF0C4A6E)
internal val Sky950 = Color(0xFF082F49)

internal object Sky : Swatch {
    override val v50 = Sky50
    override val v100 = Sky100
    override val v200 = Sky200
    override val v300 = Sky300
    override val v400 = Sky400
    override val v500 = Sky500
    override val v600 = Sky600
    override val v700 = Sky700
    override val v800 = Sky800
    override val v900 = Sky900
    override val v950 = Sky950
}

// Blue Colors
internal val Blue50 = Color(0xFFEFF6FF)
internal val Blue100 = Color(0xFFDBEAFE)
internal val Blue200 = Color(0xFFBFDBFE)
internal val Blue300 = Color(0xFF93C5FD)
internal val Blue400 = Color(0xFF60A5FA)
internal val Blue500 = Color(0xFF3B82F6)
internal val Blue600 = Color(0xFF2563EB)
internal val Blue700 = Color(0xFF1D4ED8)
internal val Blue800 = Color(0xFF1E40AF)
internal val Blue900 = Color(0xFF1E3A8A)
internal val Blue950 = Color(0xFF172554)

internal object Blue : Swatch {
    override val v50 = Blue50
    override val v100 = Blue100
    override val v200 = Blue200
    override val v300 = Blue300
    override val v400 = Blue400
    override val v500 = Blue500
    override val v600 = Blue600
    override val v700 = Blue700
    override val v800 = Blue800
    override val v900 = Blue900
    override val v950 = Blue950
}

// Indigo Colors
internal val Indigo50 = Color(0xFFEEF2FF)
internal val Indigo100 = Color(0xFFE0E7FF)
internal val Indigo200 = Color(0xFFC7D2FE)
internal val Indigo300 = Color(0xFFA5B4FC)
internal val Indigo400 = Color(0xFF818CF8)
internal val Indigo500 = Color(0xFF6366F1)
internal val Indigo600 = Color(0xFF4F46E5)
internal val Indigo700 = Color(0xFF4338CA)
internal val Indigo800 = Color(0xFF3730A3)
internal val Indigo900 = Color(0xFF312E81)
internal val Indigo950 = Color(0xFF1E1B4B)

internal object Indigo : Swatch {
    override val v50 = Indigo50
    override val v100 = Indigo100
    override val v200 = Indigo200
    override val v300 = Indigo300
    override val v400 = Indigo400
    override val v500 = Indigo500
    override val v600 = Indigo600
    override val v700 = Indigo700
    override val v800 = Indigo800
    override val v900 = Indigo900
    override val v950 = Indigo950
}

// Violet Colors
internal val Violet50 = Color(0xFFF5F3FF)
internal val Violet100 = Color(0xFFEDE9FE)
internal val Violet200 = Color(0xFFDDD6FE)
internal val Violet300 = Color(0xFFC4B5FD)
internal val Violet400 = Color(0xFFA78BFA)
internal val Violet500 = Color(0xFF8B5CF6)
internal val Violet600 = Color(0xFF7C3AED)
internal val Violet700 = Color(0xFF6D28D9)
internal val Violet800 = Color(0xFF5B21B6)
internal val Violet900 = Color(0xFF4C1D95)
internal val Violet950 = Color(0xFF2E1065)

internal object Violet : Swatch {
    override val v50 = Violet50
    override val v100 = Violet100
    override val v200 = Violet200
    override val v300 = Violet300
    override val v400 = Violet400
    override val v500 = Violet500
    override val v600 = Violet600
    override val v700 = Violet700
    override val v800 = Violet800
    override val v900 = Violet900
    override val v950 = Violet950
}

// Purple Colors
internal val Purple50 = Color(0xFFFAF5FF)
internal val Purple100 = Color(0xFFF3E8FF)
internal val Purple200 = Color(0xFFE9D5FF)
internal val Purple300 = Color(0xFFD8B4FE)
internal val Purple400 = Color(0xFFC084FC)
internal val Purple500 = Color(0xFFA855F7)
internal val Purple600 = Color(0xFF9333EA)
internal val Purple700 = Color(0xFF7E22CE)
internal val Purple800 = Color(0xFF6B21A8)
internal val Purple900 = Color(0xFF581C87)
internal val Purple950 = Color(0xFF3B0764)

internal object Purple : Swatch {
    override val v50 = Purple50
    override val v100 = Purple100
    override val v200 = Purple200
    override val v300 = Purple300
    override val v400 = Purple400
    override val v500 = Purple500
    override val v600 = Purple600
    override val v700 = Purple700
    override val v800 = Purple800
    override val v900 = Purple900
    override val v950 = Purple950
}

// Fuchsia Colors
internal val Fuchsia50 = Color(0xFFFDF4FF)
internal val Fuchsia100 = Color(0xFFFAE8FF)
internal val Fuchsia200 = Color(0xFFF5D0FE)
internal val Fuchsia300 = Color(0xFFF0ABFC)
internal val Fuchsia400 = Color(0xFFE879F9)
internal val Fuchsia500 = Color(0xFFD946EF)
internal val Fuchsia600 = Color(0xFFC026D3)
internal val Fuchsia700 = Color(0xFFA21CAF)
internal val Fuchsia800 = Color(0xFF86198F)
internal val Fuchsia900 = Color(0xFF701A75)
internal val Fuchsia950 = Color(0xFF4A044E)

internal object Fuchsia : Swatch {
    override val v50 = Fuchsia50
    override val v100 = Fuchsia100
    override val v200 = Fuchsia200
    override val v300 = Fuchsia300
    override val v400 = Fuchsia400
    override val v500 = Fuchsia500
    override val v600 = Fuchsia600
    override val v700 = Fuchsia700
    override val v800 = Fuchsia800
    override val v900 = Fuchsia900
    override val v950 = Fuchsia950
}

// Pink Colors
internal val Pink50 = Color(0xFFFDF2F8)
internal val Pink100 = Color(0xFFFCE7F3)
internal val Pink200 = Color(0xFFFBCFE8)
internal val Pink300 = Color(0xFFF9A8D4)
internal val Pink400 = Color(0xFFF472B6)
internal val Pink500 = Color(0xFFEC4899)
internal val Pink600 = Color(0xFFDB2777)
internal val Pink700 = Color(0xFFBE185D)
internal val Pink800 = Color(0xFF9D174D)
internal val Pink900 = Color(0xFF831843)
internal val Pink950 = Color(0xFF500724)

internal object Pink : Swatch {
    override val v50 = Pink50
    override val v100 = Pink100
    override val v200 = Pink200
    override val v300 = Pink300
    override val v400 = Pink400
    override val v500 = Pink500
    override val v600 = Pink600
    override val v700 = Pink700
    override val v800 = Pink800
    override val v900 = Pink900
    override val v950 = Pink950
}

// Rose Colors
internal val Rose50 = Color(0xFFFFF1F2)
internal val Rose100 = Color(0xFFFFE4E6)
internal val Rose200 = Color(0xFFFECDD3)
internal val Rose300 = Color(0xFFFDA4AF)
internal val Rose400 = Color(0xFFFB7185)
internal val Rose500 = Color(0xFFF43F5E)
internal val Rose600 = Color(0xFFE11D48)
internal val Rose700 = Color(0xFFBE123C)
internal val Rose800 = Color(0xFF9F1239)
internal val Rose900 = Color(0xFF881337)
internal val Rose950 = Color(0xFF4C0519)

internal object Rose : Swatch {
    override val v50 = Rose50
    override val v100 = Rose100
    override val v200 = Rose200
    override val v300 = Rose300
    override val v400 = Rose400
    override val v500 = Rose500
    override val v600 = Rose600
    override val v700 = Rose700
    override val v800 = Rose800
    override val v900 = Rose900
    override val v950 = Rose950
}

internal val Black = Color(0xFF000000)
internal val White = Color(0xFFFFFFFF)
internal val Transparent = Color(0x00000000)
