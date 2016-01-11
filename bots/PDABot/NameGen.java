package bots.PDABot;

public abstract class NameGen {
    private static final String mAnimalNames[] = new String[]{ "Aardvark", "Albatross", "Alligator", "Alpaca", "Ant", "Anteater", "Antelope", "Ape", "Armadillo", "Donkey", "Baboon", "Badger", "Barracuda", "Bat", "Bear", "Beaver", "Bee", "Bison", "Boar", "Buffalo", "Butterfly", "Camel", "Capybara", "Caribou", "Cassowary", "Cat", "Caterpillar", "Cattle", "Chamois", "Cheetah", "Chicken", "Chimpanzee", "Chinchilla", "Clam", "Coati", "Cobra", "Cockroach", "Cod", "Cormorant", "Coyote", "Crab", "Crane", "Crocodile", "Crow", "Curlew", "Deer", "Dinosaur", "Dog", "Dogfish", "Dolphin", "Donkey", "Dotterel", "Dove", "Dragonfly", "Duck", "Dugong", "Dunlin", "Eagle", "Echidna", "Eel", "Eland", "Elephant", "Elk", "Emu", "Falcon", "Ferret", "Finch", "Fish", "Flamingo", "Fly", "Fox", "Frog", "Gaur", "Gazelle", "Gerbil", "Giraffe", "Gnat", "Gnu", "Goat", "Goose", "Goldfinch", "Goldfish", "Gorilla", "Goshawk", "Grasshopper", "Grouse", "Guanaco", "GuineaPig", "Gull", "Hamster", "Hare", "Hawk", "Hedgehog", "Heron", "Herring", "Hippopotamus", "Hornet", "Horse", "Hummingbird", "Hyena", "Ibex", "Ibis", "Jackal", "Jaguar", "Jay", "Jellyfish", "Kangaroo", "Kingfisher", "Kinkajou", "Koala", "Kookabura", "Kouprey", "Kudu", "Lapwing", "Lark", "Lemur", "Leopard", "Lion", "Llama", "Lobster", "Locust", "Loris", "Louse", "Lyrebird", "Magpie", "Mallard", "Manatee", "Mandrill", "Mantis", "Marten", "Meerkat", "Mink", "Mole", "Mongoose", "Monkey", "Moose", "Mouse", "Mosquito", "Mule", "Narwhal", "Newt", "Nightingale", "Octopus", "Okapi", "Opossum", "Oryx", "Ostrich", "Otter", "Owl", "Oyster", "Panther", "Parrot", "Panda", "Partridge", "Peafowl", "Pelican", "Penguin", "Pheasant", "Pig", "Pigeon", "PolarBear", "Pony", "Porcupine", "Porpoise", "PrairieDog", "Quail", "Quelea", "Quetzal", "Rabbit", "Raccoon", "Rail", "Ram", "Rat", "Raven", "Reindeer", "Rhinoceros", "Rook", "Salamander", "Salmon", "Sandpiper", "Sardine", "Scorpion", "Seahorse", "Seal", "Shark", "Sheep", "Shrew", "Skunk", "Sloth", "Snail", "Snake", "Sparrow", "Spider", "Spoonbill", "Squid", "Squirrel", "Starling", "Stingray", "Stinkbug", "Stork", "Swallow", "Swan", "Tapir", "Tarsier", "Termite", "Tiger", "Toad", "Trout", "Turkey", "Turtle", "Vicuna", "Viper", "Vulture", "Wallaby", "Walrus", "Wasp", "Weasel", "Whale", "Wildcat", "Wolf", "Wolverine", "Wombat", "Woodcock", "Woodpecker", "Worm", "Wren", "Yak", "Zebra" };
    private static final String mColourNames[] = new String[]{ "White", "Silver", "Gray", "Black", "Navy", "Blue", "Cerulean", "Turquoise", "Azure", "Teal", "Cyan", "Green", "Lime", "Olive", "Yellow", "Gold", "Amber", "Orange", "Brown", "Red", "Maroon", "Rose", "Pink", "Magenta", "Purple", "Indigo", "Violet", "Peach", "Apricot", "Ochre", "Plum" };
    private static final String mAdjectives[]  = new String[]{ "Adorable", "Agreeable", "Alive", "Angry", "Beautiful", "Better", "Bewildered", "Big", "Boiling", "Brave", "Breezy", "Broken", "Bumpy", "Calm", "Careful", "Chilly", "Clean", "Clever", "Clumsy", "Cold", "Colossal", "Cool", "Creepy", "Crooked", "Cuddly", "Curly", "Damaged", "Damp", "Dead", "Defeated", "Delightful", "Dirty", "Drab", "Dry", "Dusty", "Eager", "Easy", "Elegant", "Embarrassed", "Faithful", "Famous", "Fancy", "Fat", "Fierce", "Filthy", "Flaky", "Fluffy", "Freezing", "Gentle", "Gifted", "Gigantic", "Glamorous", "Great", "Grumpy", "Handsome", "Happy", "Helpful", "Helpless", "Hot", "Huge", "Immense", "Important", "Inexpensive", "Itchy", "Jealous", "Jolly", "Kind", "Large", "Lazy", "Little", "Lively", "Long", "Magnificent", "Mammoth", "Massive", "Miniature", "Mushy", "Mysterious", "Nervous", "Nice", "Obedient", "Obnoxious", "Odd", "Panicky", "Petite", "Plain", "Powerful", "Proud", "Puny", "Quaint", "Relieved", "Repulsive", "Rich", "Scary", "Scrawny", "Short", "Shy", "Silly", "Small", "Sparkling", "Tall", "Tender", "Thankful", "Thoughtless", "Tiny", "Ugliest", "Unsightly", "Uptight", "Vast", "Victorious", "Warm", "Wet", "Witty", "Worried", "Wrong", "Zealous" };

    public static String getNewRandomName()
    {
        int animal    = (int)(Math.random() * (double)mAnimalNames.length);
        int colour    = (int)(Math.random() * (double)mColourNames.length);
        int adjective = (int)(Math.random() * (double)mAdjectives.length);

        return mAdjectives[adjective] +" "+ mColourNames[colour] +" "+ mAnimalNames[colour];
    }

    public static String getNewRandomShortName()
    {
        int animal    = (int)(Math.random() * (double)mAnimalNames.length);
        int adjective = (int)(Math.random() * (double)mAdjectives.length);

        return mAdjectives[adjective] + " " + mAnimalNames[animal];
    }
}
