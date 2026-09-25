# jairo_tableview

Aplicación de escritorio en **JavaFX** que gestiona una lista de personas en una tabla:
permite añadir filas, borrar las seleccionadas y restaurar los datos iniciales.

## Requisitos

- JDK 11 o superior
- Maven (o el gestor de build del IDE)
- JavaFX se descarga automáticamente como dependencia Maven (`javafx-controls`, `javafx-fxml` 13)

## Compilar y ejecutar

```bash
mvn clean javafx:run
```

`clean` es recomendable: elimina copias antiguas de FXML/CSS de la carpeta `target`.

**Desde VS Code** (F5 o *Run*): la clase principal es `com.jairoben.Launcher`,
configurada en `.vscode/launch.json`. Se usa un lanzador auxiliar porque JavaFX
falla con «JavaFX runtime components are missing» cuando el `main` está en una
clase que extiende `Application` y se ejecuta por classpath (como hace el JRE
embebido de la extensión de Java).

## Estructura del proyecto

```
src/main/
├── java/
│   └── com/jairoben/
│       ├── App.java                // Arranque JavaFX (escena, límites, bundle)
│       ├── Launcher.java           // Lanzador para IDEs (no extiende Application)
│       ├── Person.java             // Modelo de datos
│       └── PrimaryController.java  // Controlador de la vista principal
└── resources/
    ├── fxml/primary.fxml           // Interfaz (montada en Scene Builder)
    ├── css/estilos.css             // Hoja de estilos (borde azul)
    └── strings.properties          // Textos de la aplicación (i18n)
```

## Cómo funciona la aplicación

### Arranque (`App.java`)

1. `main()` lanza la aplicación JavaFX.
2. `start(Stage)` carga `fxml/primary.fxml` con un `FXMLLoader` y lo mete en una `Scene`.
3. Se fijan los límites de la ventana: mínimo **520×560** (para que no se recorte el
   campo *Birth Date*) y máximo **760×820** (para que no se desborde).
4. La ventana se muestra con el tamaño *preferred* del FXML (520×560).

### Interfaz (`primary.fxml`)

Raíz `BorderPane` con la clase CSS `borde-azul`:

- **`top`** → `VBox` con un `GridPane` (First Name, Last Name, Birth Date + botón *Add*)
  y un `HBox` con *Restore Rows* y *Delete Selected Rows*.
- **`center`** → `TableView` con las columnas **Id**, **First Name**, **Last Name**, **Birth Date**.

Los `fx:id` del FXML son los nombres de los campos `@FXML` del controlador
(`tfFirstName`, `tfLastName`, `dpBirth`, `btAdd`, `btRestore`, `btDeleteSelected`,
`tvPeople`, `colId`, `colFirstName`, `colLastName`, `colBirthDate`), y los `onAction`
apuntan a los métodos `#add`, `#restore` y `#deleteSelected`.

### Modelo (`Person.java`)

- `id` autoincremental (`static nextId`): no se teclea, se asigna al crear la persona.
- `firstName`, `lastName` (`String`) y `birthDate` (`LocalDate`).
- Los **getters son obligatorios**: `PropertyValueFactory` los busca por reflexión para
  rellenar cada columna de la tabla.

### Controlador (`PrimaryController.java`)

`initialize()` se ejecuta **solo**, automáticamente, cuando el `FXMLLoader` termina de
cargar el FXML, antes de que se vea la ventana. Ahí:

1. Se enlazan las 4 columnas con `new PropertyValueFactory<>("propiedad")`.
2. Se activa `SelectionMode.MULTIPLE` (Ctrl+clic / Mayús+clic para selección múltiple).
3. Se cargan las 5 filas de ejemplo en `datosIniciales` y se copian a `datos`.
4. `tvPeople.setItems(datos)` pinta la tabla.

La tabla **siempre** se pinta desde `datos`. `datosIniciales` nunca se modifica: es la
copia de seguridad que usa *Restore Rows*.

| Botón | Método | Qué hace |
|---|---|---|
| Add | `add()` | Valida que First Name, Last Name y Birth Date estén rellenos (si no, muestra un `Alert`), crea un `Person`, lo añade a `datos` y limpia los campos |
| Delete Selected Rows | `deleteSelected()` | Copia la selección a un `ArrayList` y la borra de `datos` |
| Restore Rows | `restore()` | `datos.setAll(datosIniciales)` repone las 5 filas originales |

**Detalle importante**: en `deleteSelected()` la selección se copia antes de borrar,
porque `getSelectedItems()` es una lista *viva* ligada a la tabla: si se itera sobre ella
mientras se modifica `datos`, JavaFX lanza `ConcurrentModificationException`.

### Estilos (`css/estilos.css`)

La clase `.borde-azul` dibuja el borde azul de 3 px alrededor del contenido
(`-fx-border-color: #2f6fed`). El FXML la carga con `stylesheets="@../css/estilos.css"`.

## Textos de la aplicación (`strings.properties`)

Ningún texto está hardcodeado en el código: todos viven en
`src/main/resources/strings.properties` (bundle `strings`, locale `es`).

**En Java (`App.java`):**
```java
Locale locale = Locale.forLanguageTag("es");
ResourceBundle strings = ResourceBundle.getBundle("strings", locale);
FXMLLoader fxmlLoader = new FXMLLoader(url, strings);  // se pasa al loader
stage.setTitle(strings.getString("window.title"));      // uso directo
```

**En el FXML** se usa el prefijo `%` + la clave:
```xml
<Label text="%label.first.name" />
<Button text="%button.add" />
```
El `FXMLLoader` resuelve `%clave` contra el bundle que recibió.

**En el controlador** se usa el campo `resources`, que el `FXMLLoader` inyecta
solo antes de llamar a `initialize()`:
```java
@FXML
private ResourceBundle resources;
...
resources.getString("aviso.campos.obligatorios")
```

## Convenciones de nombres usadas

| Prefijo | Tipo | Ejemplo |
|---|---|---|
| `tf` | TextField | `tfFirstName` |
| `dp` | DatePicker | `dpBirth` |
| `bt` | Button | `btAdd`, `btRestore` |
| `tv` | TableView | `tvPeople` |
| `col` | TableColumn | `colLastName` |
