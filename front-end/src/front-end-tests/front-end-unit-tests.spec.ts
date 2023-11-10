import { test, expect } from "@playwright/test";

/**
 * This sets up the url for each test
 */
test.beforeEach(async ({ page }, testInfo) => {
  await page.goto("http://localhost:2005/");
});

test("on page load, I see an input bar", async ({ page }) => {
  // Notice: http, not https! Our front-end is not set up for HTTPs.
  await expect(page.getByLabel("Command input")).toBeVisible();
});

test("after I type into the input box, its text changes", async ({ page }) => {
  // Step 2: Interact with the page
  // Locate the element you are looking for
  await page.getByLabel("Command input").click();
  await page.getByLabel("Command input").fill("Awesome command");

  // Step 3: Assert something about the page
  // Assertions are done by using the expect() function
  const mock_input = `Awesome command`;
  await expect(page.getByLabel("Command input")).toHaveValue(mock_input);
});

test ("'basic test", async ({page}) => {
  await page.goto("https: //playwright. dev/docs/intro")
  const docsLink = page. locator("a >> text=Docs")
  const color = await docsLink.evaluate( (e) => {
    return window. getComputedStyle(e).getPropertyValue("color")
  })
  expect (color). toBe ("rgb (69, 186, 75)")
})

test("integrated test of broadband data search", async ({ page }) => {
  //running a broadband search
  //checking valid county
  await page.getByLabel("Command input").fill("broadband California Orange");
  await page.getByRole("button", { name: "Submit" }).click();

  const output6 = "% Broadband Access: 93.0"
  await expect(page.getByLabel("Command history")).toContainText(output6);

  //checking invalid county
  await page.getByLabel("Command input").fill("broadband California Yellow");
  await page.getByRole("button", { name: "Submit" }).click();

  const output4 = "Invalid county."
  await expect(page.getByLabel("Command history")).toContainText(output4);
});