<x-app-layout>
    <x-slot name="header">
        <div class="flex justify-between">
            <h2 class="font-semibold text-xl text-gray-800 leading-tight">
                {{ __('Caring Types') }}
            </h2>
        </div>
    </x-slot>
    <x-splade-modal position="top" max-width="2xl">
        <div class="py-10">
            <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
                <div class="bg-white overflow-hidden shadow-sm sm:rounded-lg">
                    <div class="p-1 text-gray-900">
                        <h1 class="text-center font-bold">Create Caring Type</h1>
                        <x-splade-form confirm :action="route('caringtypes.store')" method="POST" class="max-w-md mx-auto p-4 bg-white rounded-md">
                            @csrf
                            <x-splade-input name="name" label="Name" required />
                            <x-splade-textarea name="description" label="Description" max="300" autosize  />
                            <x-splade-submit class="mt-4"/>
                        </x-splade-form>
                    </div>
                </div>
            </div>
        </div>
    </x-splade-modal>
</x-app-layout>
