package com.drumclick

/** Scalette importate (BPM da fonti online, da verificare a orecchio; link = ricerca YouTube). */
object Seed {
    val lists = listOf(
        SetList("Apericena", listOf(
            Song("Easy – Commodores", 65, 4, 1, "https://www.youtube.com/results?search_query=Easy+Commodores+official"),
            Song("Just the Two of Us – Grover Washington Jr. & Bill Withers", 96, 4, 1, "https://www.youtube.com/results?search_query=Just+the+Two+of+Us+Grover+Washington+Jr.+%26+Bill+Withers+official"),
            Song("Ain't No Sunshine – Bill Withers", 80, 4, 1, "https://www.youtube.com/results?search_query=Ain%27t+No+Sunshine+Bill+Withers+official"),
            Song("(Sittin' On) The Dock of the Bay – Otis Redding", 103, 4, 1, "https://www.youtube.com/results?search_query=%28Sittin%27+On%29+The+Dock+of+the+Bay+Otis+Redding+official"),
            Song("Budapest – George Ezra", 128, 4, 1, "https://www.youtube.com/results?search_query=Budapest+George+Ezra+official"),
            Song("Always Remember Us This Way – Lady Gaga", 65, 4, 1, "https://www.youtube.com/results?search_query=Always+Remember+Us+This+Way+Lady+Gaga+official"),
            Song("Have You Ever Seen the Rain – Creedence Clearwater Revival", 116, 4, 1, "https://www.youtube.com/results?search_query=Have+You+Ever+Seen+the+Rain+Creedence+Clearwater+Revival+official"),
            Song("Stay – Rihanna", 112, 4, 1, "https://www.youtube.com/results?search_query=Stay+Rihanna+official"),
            Song("La Flaca – Jarabe de Palo", 103, 4, 1, "https://www.youtube.com/results?search_query=La+Flaca+Jarabe+de+Palo+official"),
            Song("House of the Rising Sun – The Animals", 117, 6, 1, "https://www.youtube.com/results?search_query=House+of+the+Rising+Sun+The+Animals+official"),
            Song("Jolene – Dolly Parton", 110, 4, 1, "https://www.youtube.com/results?search_query=Jolene+Dolly+Parton+official"),
            Song("I Wanna Be Like You – Louis Prima", 192, 4, 1, "https://www.youtube.com/results?search_query=I+Wanna+Be+Like+You+Louis+Prima+official"),
            Song("Human – Rag'n'Bone Man", 75, 4, 1, "https://www.youtube.com/results?search_query=Human+Rag%27n%27Bone+Man+official"),
            Song("Lemon Tree – Fool's Garden", 143, 4, 1, "https://www.youtube.com/results?search_query=Lemon+Tree+Fool%27s+Garden+official"),
            Song("Lose Control – Teddy Swims", 80, 4, 1, "https://www.youtube.com/results?search_query=Lose+Control+Teddy+Swims+official"),
            Song("A Night Like This – Caro Emerald", 123, 4, 1, "https://www.youtube.com/results?search_query=A+Night+Like+This+Caro+Emerald+official"),
            Song("That's Life – Frank Sinatra", 75, 4, 1, "https://www.youtube.com/results?search_query=That%27s+Life+Frank+Sinatra+official"),
            Song("Can't Help Falling in Love – Elvis Presley", 89, 4, 1, "https://www.youtube.com/results?search_query=Can%27t+Help+Falling+in+Love+Elvis+Presley+official"),
            Song("Dream a Little Dream – The Mamas & the Papas", 89, 4, 1, "https://www.youtube.com/results?search_query=Dream+a+Little+Dream+The+Mamas+%26+the+Papas+official"),
            Song("L-O-V-E – Nat King Cole", 155, 4, 1, "https://www.youtube.com/results?search_query=L-O-V-E+Nat+King+Cole+official"),
            Song("Candy – Robbie Williams", 116, 4, 1, "https://www.youtube.com/results?search_query=Candy+Robbie+Williams+official"),
            Song("Prima di andare via – Neffa", 111, 4, 1, "https://www.youtube.com/results?search_query=Prima+di+andare+via+Neffa+official")
        )),
        SetList("Matrimonio dance", listOf(
            Song("Can't Take My Eyes Off You – Frankie Valli", 124, 4, 1, "https://www.youtube.com/results?search_query=Can%27t+Take+My+Eyes+Off+You+Frankie+Valli+official"),
            Song("Fresh – Kool & The Gang", 117, 4, 1, "https://www.youtube.com/results?search_query=Fresh+Kool+%26+The+Gang+official"),
            Song("You Make Me Feel Like Dancing – Leo Sayer", 96, 4, 1, "https://www.youtube.com/results?search_query=You+Make+Me+Feel+Like+Dancing+Leo+Sayer+official"),
            Song("Long Train Runnin' – The Doobie Brothers", 118, 4, 1, "https://www.youtube.com/results?search_query=Long+Train+Runnin%27+The+Doobie+Brothers+official"),
            Song("Boogie Man – AC/DC", 112, 4, 1, "https://www.youtube.com/results?search_query=Boogie+Man+AC%2FDC+official"),
            Song("Superstition – Stevie Wonder", 100, 4, 1, "https://www.youtube.com/results?search_query=Superstition+Stevie+Wonder+official"),
            Song("Jailhouse Rock – Elvis Presley", 168, 4, 1, "https://www.youtube.com/results?search_query=Jailhouse+Rock+Elvis+Presley+official"),
            Song("Hound Dog – Elvis Presley", 175, 4, 1, "https://www.youtube.com/results?search_query=Hound+Dog+Elvis+Presley+official"),
            Song("These Boots Are Made for Walkin' – Nancy Sinatra", 112, 4, 1, "https://www.youtube.com/results?search_query=These+Boots+Are+Made+for+Walkin%27+Nancy+Sinatra+official"),
            Song("Volare / Bamboleo – Gipsy Kings", 120, 4, 1, "https://www.youtube.com/results?search_query=Volare+%2F+Bamboleo+Gipsy+Kings+official"),
            Song("Crying at the Discoteque – Alcazar", 138, 4, 1, "https://www.youtube.com/results?search_query=Crying+at+the+Discoteque+Alcazar+official"),
            Song("Born to Be Alive – Patrick Hernandez", 132, 4, 1, "https://www.youtube.com/results?search_query=Born+to+Be+Alive+Patrick+Hernandez+official"),
            Song("Hot Stuff – Donna Summer", 121, 4, 1, "https://www.youtube.com/results?search_query=Hot+Stuff+Donna+Summer+official"),
            Song("Che idea (Ma quale idea) – Pino D'Angiò", 114, 4, 1, "https://www.youtube.com/results?search_query=Che+idea+%28Ma+quale+idea%29+Pino+D%27Angi%C3%B2+official"),
            Song("Get Down on It – Kool & The Gang", 110, 4, 1, "https://www.youtube.com/results?search_query=Get+Down+on+It+Kool+%26+The+Gang+official"),
            Song("September – Earth, Wind & Fire", 126, 4, 1, "https://www.youtube.com/results?search_query=September+Earth%2C+Wind+%26+Fire+official"),
            Song("Le Freak – Chic", 120, 4, 1, "https://www.youtube.com/results?search_query=Le+Freak+Chic+official"),
            Song("We Are Family – Sister Sledge", 119, 4, 1, "https://www.youtube.com/results?search_query=We+Are+Family+Sister+Sledge+official"),
            Song("Celebrate (Celebration) – Kool & The Gang", 121, 4, 1, "https://www.youtube.com/results?search_query=Celebrate+%28Celebration%29+Kool+%26+The+Gang+official"),
            Song("Figli delle stelle – Alan Sorrenti", 108, 4, 1, "https://www.youtube.com/results?search_query=Figli+delle+stelle+Alan+Sorrenti+official"),
            Song("Daddy Cool – Boney M.", 125, 4, 1, "https://www.youtube.com/results?search_query=Daddy+Cool+Boney+M.+official"),
            Song("50 Special – Lùnapop", 160, 4, 1, "https://www.youtube.com/results?search_query=50+Special+L%C3%B9napop+official"),
            Song("Disco Inferno – The Trammps", 129, 4, 1, "https://www.youtube.com/results?search_query=Disco+Inferno+The+Trammps+official"),
            Song("I Feel Good – James Brown", 145, 4, 1, "https://www.youtube.com/results?search_query=I+Feel+Good+James+Brown+official")
        ))
    )
}
