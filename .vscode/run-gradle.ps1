$version = '8.11.1'
$candidates = [System.Collections.Generic.List[string]]::new()

if ($env:GRADLE_HOME) {
    $candidates.Add((Join-Path $env:GRADLE_HOME 'bin\gradle.bat'))
}

if ($env:USERPROFILE) {
    $wrapperCache = Join-Path $env:USERPROFILE ".gradle\wrapper\dists\gradle-$version-bin"
    if (Test-Path $wrapperCache) {
        Get-ChildItem -LiteralPath $wrapperCache -Directory | ForEach-Object {
            $candidates.Add((Join-Path $_.FullName "gradle-$version\bin\gradle.bat"))
        }
    }
}

$pathGradle = Get-Command gradle.bat -ErrorAction SilentlyContinue
if ($pathGradle) {
    $candidates.Add($pathGradle.Source)
}

$installedGradle = $candidates | Select-Object -Unique | Where-Object { Test-Path -LiteralPath $_ } | Select-Object -First 1
if ($installedGradle) {
    & $installedGradle @args
    exit $LASTEXITCODE
}

$wrapper = Join-Path $PSScriptRoot '..\gradlew.bat'
& $wrapper @args
exit $LASTEXITCODE
