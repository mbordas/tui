The purpose of this lib is to provide a simple yet testable way to build web UI. The API is pure Java, it generates semantic HTML, embeds
native Javascript, uses json for exchanges with backend.

# Demos

Go to test environment, the package *tui.demo* contains some runnable code that shows how to use components and layouts.

# Building a UI

TUI provides 2 types of objects to create a UI: the components and the layouts.
The components are shown (buttons, paragraphs, forms, etc) and layouts are used to position the components in the rendered space.

## Page

The key (and starting) object of a TUI interface is the Page. Give it a title, add a header or a footer, then layouts and components.

## Style

Your backend needs a Style instance to send a page to the browser. This Style can be embedded in the HTML or given as a CSS file.

## Components

### Form

There are 2 types of form to build: a regular Form or a ModalForm. Both are built and used the same way.
<br>You will control inputs values at backend-side and return success or errors.
<br>You can return a form update when you want the user to follow multiple steps.

### Image

Just an image.

### NavButton

The NavButton replaces the hyperlink (NavLink) when you want to add parameters to the opened page but you don't want them to be in the URL.

### NavLink

Just a hyperlink.

### Paragraph

Where you put your texts.

### RefreshButton

Connect it to other refreshable components.

### Section

Create a section in your flow, that mean with a title.

### SVG

Draw the graphics you want to include.

### Table

Displays data (strings only) in a table, with title, legend, headers.

### TablePicker

This is a Table which reacts when the user clicks on it: the connected components are refreshed. The values of the clicked row are sent to
the backend as parameters of the refresh requests.

## Layouts

### Grid

You can use Grid when you want to organize some elements as if it were in a table.
<br>You can set a specific width to the first column only.

### Panel

The Panel is a very simple container for components. You can use it in order to create a horizontal flow of components (in cse the
components do not use the whole width of their containers). Or simply use it to group components, which may be very useful when coding
updates.

### TabbedFlow

Create a vertical flow which is split into labelled tabs. Only the selected tab is shown at a time.

### VerticalFlow

This is the default layout used at Page level. All the components and layouts it contains are appended each under its predecessor.
<br>You can customize its *width*, acting on its margins on both sides.
<br>You can customize its *spacing*, telling it how much space you want between the components that are contained in the flow.

### VerticalScroll

This container allows to fix its height so that all its content can be scrolled.
