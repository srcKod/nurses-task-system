<x-app-layout>
    <x-slot name="header">
        <div class="flex justify-between">
            <h2 class="font-semibold text-xl text-gray-800 leading-tight">
                {{ __('Nurses') }}
            </h2>
        </div>
    </x-slot>
    <x-splade-modal position="top" max-width="2xl">
        <div class="py-10">
            <div class="max-w-7xl mx-auto sm:px-6 lg:px-8">
                <div class="bg-white overflow-hidden shadow-sm sm:rounded-lg">
                    <div class="p-1 text-gray-900">
                        <h1 class="text-center font-bold">Create Nurse</h1>
                        <x-splade-form confirm :action="route('nurses.store')" method="POST" class="max-w-md mx-auto p-4 bg-white rounded-md">
                            @csrf
                            <x-splade-input name="name" label="Name" max="50" required />
                            <x-splade-input name="phone" label="Phone" type="number" />
                            <x-splade-input name="email" label="email" type="email" required/>
                            <x-splade-input name="password" label="password" type="password" min="8" required/>
                            <x-splade-select name="gender" label="Gender">
                                <option value="1">Male</option>
                                <option value="0">Female</option>
                            </x-splade-select>
                            <x-splade-select name="is_admin" label="Role">
                                <option value="1">Admin</option>
                                <option value="0">User</option>
                            </x-splade-select>
                            <x-splade-select name="is_resigned" label="Statue">
                                <option value="1">Resigned</option>
                                <option value="0">Not Resigned</option>
                            </x-splade-select>
                            <x-splade-submit class="mt-4" />
                        </x-splade-form>
                    </div>
                </div>
            </div>
        </div>
    </x-splade-modal>
</x-app-layout>
